#!/usr/bin/env python3
"""
Large File Handler - Intelligent Graceful Degradation Strategy

When editing files >500 lines, automatically activates multi-level fallback
to avoid edit/write dead loops.

Usage:
    python large_file_handler.py edit --file <filepath> --new-content <content_path>
    python large_file_handler.py analyze --file <filepath>
"""

import re
import os
import sys
import shutil
import hashlib
import difflib
import argparse
import json
import logging
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Optional, Tuple, Any


# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('large_file_handler.log'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)


class LargeFileHandler:
    """
    Detection and triggering mechanism for large file operations.
    """
    
    def __init__(self, max_lines: int = 500, max_retries: int = 2):
        self.max_lines = max_lines
        self.max_retries = max_retries
        self.retry_count = 0
        self.strategies = [
            "direct_edit",
            "four_step_anti_loop",
            "chunk_processing",
            "incremental_edit",
            "context_optimization",
            "script_assist"
        ]
    
    def should_activate(self, content: str, operation: str) -> bool:
        """
        Detect if large file handling strategy should be activated.
        
        Args:
            content: File content to check
            operation: Type of operation (edit, write, rewrite, refactor)
            
        Returns:
            True if large file strategy should be activated
        """
        lines = content.count('\n') + 1
        is_large = lines > self.max_lines
        
        # Check if high-risk operation
        high_risk_ops = ['edit', 'write', 'rewrite', 'refactor']
        is_high_risk = any(op in operation.lower() for op in high_risk_ops)
        
        if is_large and is_high_risk:
            logger.info(f"Large file detected: {lines} lines, operation: {operation}")
        
        return is_large and is_high_risk
    
    def get_file_stats(self, content: str) -> Dict:
        """Get statistics about file content."""
        lines = content.split('\n')
        return {
            'total_lines': len(lines),
            'total_chars': len(content),
            'non_empty_lines': len([l for l in lines if l.strip()]),
            'estimated_tokens': len(content) // 4  # Rough estimate
        }


class LargeFileController:
    """
    Main controller for executing file operations with graceful degradation.
    """
    
    def __init__(self, config_path: Optional[str] = None):
        self.handler = LargeFileHandler()
        self.state = {
            'original_content': '',
            'modified_content': '',
            'attempts': [],
            'failed_operations': [],
            'backup_path': None
        }
        self.config = self._load_config(config_path)
        
    def _load_config(self, config_path: Optional[str]) -> Dict:
        """Load configuration from YAML file or use defaults."""
        defaults = {
            'max_lines': 500,
            'chunk_size': 200,
            'overlap_lines': 10,
            'context_lines': 50,
            'auto_execute': False,
            'strategies': {
                'four_step_anti_loop': {'enabled': True, 'timeout': 30},
                'chunk_processing': {'enabled': True, 'chunk_size': 200},
                'incremental_edit': {'enabled': True, 'max_operations': 50},
                'context_optimization': {'enabled': True, 'context_lines': 50},
                'script_assist': {'enabled': True, 'auto_execute': False}
            }
        }
        
        if config_path and os.path.exists(config_path):
            try:
                import yaml
                with open(config_path, 'r') as f:
                    config = yaml.safe_load(f)
                    if config and 'large_file_handler' in config:
                        return {**defaults, **config['large_file_handler']}
            except ImportError:
                logger.warning("PyYAML not installed, using defaults")
            except Exception as e:
                logger.error(f"Error loading config: {e}")
        
        return defaults
    
    def execute_with_graceful_degradation(self, 
                                         operation: str, 
                                         filepath: str,
                                         new_content: Optional[str] = None) -> Dict:
        """
        Execute file operation with graceful degradation strategies.
        
        Args:
            operation: Type of operation
            filepath: Path to file
            new_content: New content to write (if applicable)
            
        Returns:
            Result dictionary with success status and details
        """
        # Read original content
        if os.path.exists(filepath):
            with open(filepath, 'r', encoding='utf-8') as f:
                original_content = f.read()
        else:
            original_content = ""
        
        self.state['original_content'] = original_content
        
        # Check if degradation needed
        if not self.handler.should_activate(original_content, operation):
            logger.info("File size within limits, attempting direct operation")
            return self._try_direct_operation(operation, filepath, new_content)
        
        logger.warning("Large file operation detected, activating graceful degradation...")
        
        # Try strategies in order
        strategies_to_try = [
            ('four_step_anti_loop', self._execute_four_step_strategy),
            ('chunk_processing', self._execute_chunk_processing),
            ('incremental_edit', self._execute_incremental_edit),
            ('context_optimization', self._execute_context_optimization),
            ('script_assist', self._execute_script_assist)
        ]
        
        result = None
        for strategy_name, strategy_func in strategies_to_try:
            if not self.config.get('strategies', {}).get(strategy_name, {}).get('enabled', True):
                continue
                
            logger.info(f"Attempting strategy: {strategy_name}")
            
            try:
                result = strategy_func(operation, filepath, new_content)
                
                if result['success']:
                    logger.info(f"Strategy {strategy_name} succeeded!")
                    return result
                else:
                    logger.warning(f"Strategy {strategy_name} failed: {result.get('error', 'Unknown error')}")
                    self.state['failed_operations'].append({
                        'strategy': strategy_name,
                        'error': result.get('error', 'Unknown error')
                    })
            except Exception as e:
                logger.error(f"Strategy {strategy_name} threw exception: {e}")
                self.state['failed_operations'].append({
                    'strategy': strategy_name,
                    'error': str(e)
                })
        
        # All strategies failed
        logger.error("All strategies failed, entering final fallback")
        return self._final_fallback(original_content, new_content, filepath)
    
    def _try_direct_operation(self, operation: str, filepath: str, new_content: Optional[str] = None) -> Dict:
        """Try direct file operation."""
        try:
            if operation == 'edit' or operation == 'write':
                if new_content is not None:
                    with open(filepath, 'w', encoding='utf-8') as f:
                        f.write(new_content)
                    return {'success': True, 'strategy': 'direct_edit'}
            return {'success': False, 'error': f'Unknown operation: {operation}'}
        except Exception as e:
            return {'success': False, 'error': f'Direct operation failed: {str(e)}'}
    
    # ============================================================================
    # STRATEGY 1: Four-Step Anti-Loop Method
    # ============================================================================
    
    def _execute_four_step_strategy(self, operation: str, filepath: str, new_content: Optional[str]) -> Dict:
        """
        Execute four-step anti-loop strategy.
        
        Steps:
        1. Analysis - Analyze file structure and changes
        2. Preparation - Create backup and plan
        3. Execution - Apply changes
        4. Verification - Validate results
        """
        steps = [
            ("Analysis", self._analysis_phase),
            ("Preparation", self._preparation_phase),
            ("Execution", self._execution_phase),
            ("Verification", self._verification_phase)
        ]
        
        results = []
        for step_name, step_func in steps:
            logger.info(f"Executing step: {step_name}")
            step_result = step_func(filepath, new_content)
            results.append({
                'step': step_name,
                'result': step_result
            })
            
            if not step_result['success']:
                return {
                    'success': False,
                    'strategy': 'four_step_anti_loop',
                    'error': f"Step {step_name} failed: {step_result.get('error', 'Unknown')}",
                    'results': results
                }
        
        return {
            'success': True,
            'strategy': 'four_step_anti_loop',
            'results': results
        }
    
    def _analysis_phase(self, filepath: str, new_content: Optional[str]) -> Dict:
        """Step 1: Analyze file structure and change scope."""
        try:
            # Get file structure (classes and functions)
            original = self.state['original_content']
            
            # Find structural elements
            structure = []
            for i, line in enumerate(original.split('\n')[:100], 1):
                if re.match(r'^(class|def|function|interface)\s+', line.strip()):
                    structure.append(f"Line {i}: {line.strip()[:80]}")
            
            # Calculate change metrics
            lines_original = len(original.split('\n'))
            lines_new = len(new_content.split('\n')) if new_content else 0
            diff_size = abs(lines_new - lines_original)
            
            # Analyze change pattern
            suggestion = self._analyze_change_pattern(original, new_content)
            
            return {
                'success': True,
                'structure_preview': structure[:20],
                'metrics': {
                    'original_lines': lines_original,
                    'new_lines': lines_new,
                    'diff_size': diff_size
                },
                'suggestion': suggestion
            }
        except Exception as e:
            return {'success': False, 'error': f"Analysis failed: {str(e)}"}
    
    def _analyze_change_pattern(self, original: str, new_content: Optional[str]) -> str:
        """Analyze the pattern of changes between original and new content."""
        if not original or not new_content:
            return "New file creation"
        
        original_lines = set(original.split('\n'))
        new_lines = set(new_content.split('\n'))
        
        added = len(new_lines - original_lines)
        removed = len(original_lines - new_lines)
        
        if added > 0 and removed == 0:
            return "Append-only changes"
        elif added == 0 and removed > 0:
            return "Deletion-only changes"
        elif added > removed * 2:
            return "Major additions with some deletions"
        elif removed > added * 2:
            return "Major deletions with some additions"
        else:
            return "Mixed modifications"
    
    def _preparation_phase(self, filepath: str, new_content: Optional[str]) -> Dict:
        """Step 2: Create backup and generate modification plan."""
        try:
            # Create backup
            timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
            content_hash = hashlib.md5(new_content.encode() if new_content else b'').hexdigest()[:8]
            backup_path = f"{filepath}.backup_{timestamp}_{content_hash}"
            
            if os.path.exists(filepath):
                shutil.copy2(filepath, backup_path)
                self.state['backup_path'] = backup_path
            
            # Generate modification plan
            plan = self._generate_modification_plan(filepath, new_content)
            
            return {
                'success': True,
                'backup_path': backup_path,
                'modification_plan': plan
            }
        except Exception as e:
            return {'success': False, 'error': f"Preparation failed: {str(e)}"}
    
    def _generate_modification_plan(self, filepath: str, new_content: Optional[str]) -> Dict:
        """Generate a plan for applying modifications."""
        original_lines = self.state['original_content'].split('\n')
        new_lines = new_content.split('\n') if new_content else []
        
        return {
            'file': filepath,
            'original_line_count': len(original_lines),
            'new_line_count': len(new_lines),
            'estimated_chunks': max(1, len(new_lines) // 200),
            'risk_level': 'high' if len(new_lines) > 1000 else 'medium' if len(new_lines) > 500 else 'low'
        }
    
    def _execution_phase(self, filepath: str, new_content: Optional[str]) -> Dict:
        """Step 3: Execute the modification."""
        if new_content is None:
            return {'success': False, 'error': "No new content provided"}
        
        try:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(new_content)
            
            return {
                'success': True,
                'bytes_written': len(new_content.encode('utf-8')),
                'lines_written': new_content.count('\n') + 1
            }
        except Exception as e:
            return {'success': False, 'error': f"Execution failed: {str(e)}"}
    
    def _verification_phase(self, filepath: str, new_content: Optional[str]) -> Dict:
        """Step 4: Verify the modification."""
        try:
            with open(filepath, 'r', encoding='utf-8') as f:
                actual_content = f.read()
            
            # Check if content matches
            if actual_content != new_content:
                return {
                    'success': False,
                    'error': 'Content mismatch after write'
                }
            
            # Basic sanity checks
            checks = {
                'file_exists': os.path.exists(filepath),
                'not_empty': len(actual_content) > 0,
                'proper_encoding': True  # If we could read it, encoding is OK
            }
            
            return {
                'success': all(checks.values()),
                'checks': checks,
                'file_size': os.path.getsize(filepath)
            }
        except Exception as e:
            return {'success': False, 'error': f"Verification failed: {str(e)}"}
    
    # ============================================================================
    # STRATEGY 2: Chunk Processing
    # ============================================================================
    
    def _execute_chunk_processing(self, operation: str, filepath: str, new_content: Optional[str]) -> Dict:
        """
        Execute chunk processing strategy.
        
        Splits large modifications into smaller chunks (~200 lines)
        and processes them sequentially.
        """
        chunk_size = self.config.get('chunk_size', 200)
        
        if not new_content:
            return {'success': False, 'error': 'No new content provided'}
        
        original_lines = self.state['original_content'].split('\n')
        new_lines = new_content.split('\n')
        
        # Check if we can use simple diff approach
        diff = list(difflib.unified_diff(
            original_lines,
            new_lines,
            lineterm=''
        ))
        
        changed_lines = [d for d in diff if d.startswith('+') and not d.startswith('+++')]
        
        if len(changed_lines) < chunk_size:
            # Small change, try direct operation
            logger.info(f"Change size ({len(changed_lines)} lines) under threshold, using direct edit")
            return self._try_direct_operation(operation, filepath, new_content)
        
        # Split into chunks
        chunks = self._split_into_chunks(new_content, chunk_size)
        logger.info(f"Split into {len(chunks)} chunks")
        
        # Process chunks
        results = []
        for i, chunk in enumerate(chunks):
            logger.info(f"Processing chunk {i+1}/{len(chunks)} (lines {chunk['start_line']}-{chunk['end_line']})")
            
            chunk_result = self._apply_chunk(filepath, chunk, i == 0)
            results.append({
                'chunk_index': i,
                'result': chunk_result
            })
            
            if not chunk_result['success']:
                # Attempt rollback
                logger.error(f"Chunk {i+1} failed, attempting rollback")
                self._rollback_to_backup(filepath)
                return {
                    'success': False,
                    'strategy': 'chunk_processing',
                    'error': f"Chunk {i+1} failed: {chunk_result.get('error', 'Unknown')}",
                    'partial_results': results
                }
        
        return {
            'success': True,
            'strategy': 'chunk_processing',
            'chunks_processed': len(chunks),
            'results': results
        }
    
    def _split_into_chunks(self, content: Optional[str], chunk_size: int) -> List[Dict]:
        """Split content into logical chunks."""
        if content is None:
            return []
        lines = content.split('\n')
        chunks = []
        
        i = 0
        while i < len(lines):
            # Try to find logical boundary
            end_idx = min(i + chunk_size, len(lines))
            
            # Look for a good breaking point (empty line or end of block)
            if end_idx < len(lines):
                for j in range(end_idx, min(end_idx + 20, len(lines))):
                    if lines[j].strip() == '' or lines[j].startswith('class ') or lines[j].startswith('def '):
                        end_idx = j + 1
                        break
            
            chunk_lines = lines[i:end_idx]
            
            chunks.append({
                'start_line': i + 1,
                'end_line': end_idx,
                'content': '\n'.join(chunk_lines),
                'is_first': i == 0,
                'is_last': end_idx >= len(lines)
            })
            
            i = end_idx
        
        return chunks
    
    def _apply_chunk(self, filepath: str, chunk: Dict, is_first: bool) -> Dict:
        """Apply a single chunk to the file."""
        try:
            if is_first:
                # First chunk: write fresh
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(chunk['content'])
            else:
                # Subsequent chunks: append
                with open(filepath, 'a', encoding='utf-8') as f:
                    f.write('\n' + chunk['content'])
            
            return {'success': True}
        except Exception as e:
            return {'success': False, 'error': str(e)}
    
    # ============================================================================
    # STRATEGY 3: Incremental Edit
    # ============================================================================
    
    def _execute_incremental_edit(self, operation: str, filepath: str, new_content: Optional[str]) -> Dict:
        """
        Execute incremental editing strategy.
        
        Applies changes as a series of small, discrete operations.
        """
        if not new_content:
            return {'success': False, 'error': 'No new content provided'}
        
        original_lines = self.state['original_content'].split('\n')
        new_lines = new_content.split('\n')
        
        # Generate diff
        diff = list(difflib.unified_diff(
            original_lines,
            new_lines,
            fromfile='original',
            tofile='modified',
            lineterm=''
        ))
        
        # Parse diff into operations
        operations = self._parse_diff_to_operations(diff)
        
        if not operations:
            logger.info("No changes detected")
            return {'success': True, 'strategy': 'incremental_edit', 'operations_applied': 0}
        
        logger.info(f"Parsed {len(operations)} incremental operations")
        
        # Apply operations
        results = []
        for i, op in enumerate(operations):
            logger.debug(f"Applying operation {i+1}/{len(operations)}: {op['type']} at line {op['line']}")
            
            op_result = self._apply_incremental_operation(filepath, op)
            results.append({
                'operation_index': i,
                'operation': op,
                'result': op_result
            })
            
            if not op_result['success']:
                return {
                    'success': False,
                    'strategy': 'incremental_edit',
                    'error': f"Operation {i+1} failed: {op_result.get('error', 'Unknown')}",
                    'applied_operations': results
                }
        
        return {
            'success': True,
            'strategy': 'incremental_edit',
            'operations_applied': len(operations),
            'results': results
        }
    
    def _parse_diff_to_operations(self, diff: List[str]) -> List[Dict]:
        """Parse unified diff into discrete operations."""
        operations = []
        current_line = 0
        
        for line in diff:
            if line.startswith('@@'):
                # Parse hunk header
                match = re.match(r'@@ -(\d+)(?:,\d+)? \+(\d+)(?:,\d+)? @@', line)
                if match:
                    current_line = int(match.group(1))
            elif line.startswith('+') and not line.startswith('+++'):
                # Addition
                operations.append({
                    'type': 'add',
                    'line': current_line,
                    'content': line[1:]
                })
                current_line += 1
            elif line.startswith('-') and not line.startswith('---'):
                # Deletion
                operations.append({
                    'type': 'delete',
                    'line': current_line,
                    'content': line[1:]
                })
            elif not line.startswith('\\'):
                # Context line
                current_line += 1
        
        return operations
    
    def _apply_incremental_operation(self, filepath: str, operation: Dict) -> Dict:
        """Apply a single incremental operation."""
        try:
            with open(filepath, 'r', encoding='utf-8') as f:
                lines = f.read().split('\n')
            
            line_idx = operation['line'] - 1
            
            if operation['type'] == 'add':
                if 0 <= line_idx <= len(lines):
                    lines.insert(line_idx, operation['content'])
            elif operation['type'] == 'delete':
                if 0 <= line_idx < len(lines):
                    del lines[line_idx]
            
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write('\n'.join(lines))
            
            return {'success': True}
        except Exception as e:
            return {'success': False, 'error': str(e)}
    
    # ============================================================================
    # STRATEGY 4: Context Optimization
    # ============================================================================
    
    def _execute_context_optimization(self, operation: str, filepath: str, new_content: Optional[str]) -> Dict:
        """
        Execute context optimization strategy.
        
        Identifies relevant sections and applies targeted modifications.
        """
        if not new_content:
            return {'success': False, 'error': 'No new content provided'}
        
        # Identify relevant context
        context_info = self._extract_relevant_context(filepath, new_content)
        
        if not context_info['relevant_sections']:
            logger.warning("Could not extract relevant context, falling back")
            return {'success': False, 'error': 'Unable to extract relevant context'}
        
        logger.info(f"Identified {len(context_info['relevant_sections'])} relevant sections")
        
        # Apply optimized modifications
        optimized_modifications = []
        for section in context_info['relevant_sections']:
            logger.info(f"Processing section: {section['name']}")
            
            optimized = self._generate_context_aware_modification(
                filepath,
                section,
                new_content
            )
            
            if optimized:
                result = self._try_targeted_edit(filepath, optimized)
                optimized_modifications.append({
                    'section': section,
                    'result': result
                })
                
                if not result['success']:
                    break
        
        # Check results
        successful_mods = [m for m in optimized_modifications if m['result']['success']]
        
        if successful_mods:
            return {
                'success': True,
                'strategy': 'context_optimization',
                'sections_modified': len(successful_mods),
                'total_sections': len(context_info['relevant_sections']),
                'modifications': optimized_modifications
            }
        
        return {
            'success': False,
            'error': 'Context optimization failed for all sections',
            'strategy': 'context_optimization',
            'modifications': optimized_modifications
        }
    
    def _extract_relevant_context(self, filepath: str, new_content: str) -> Dict:
        """Extract relevant sections from file."""
        original = self.state['original_content']
        
        # Find modified functions/classes
        sections = []
        
        # Simple heuristic: look for named blocks
        pattern = r'^(def|class|function|interface)\s+(\w+)'
        for match in re.finditer(pattern, new_content, re.MULTILINE):
            block_type = match.group(1)
            block_name = match.group(2)
            
            sections.append({
                'type': block_type,
                'name': block_name,
                'pattern': f"^{block_type}\\s+{block_name}"
            })
        
        return {
            'relevant_sections': sections,
            'total_sections_found': len(sections)
        }
    
    def _generate_context_aware_modification(self, filepath: str, section: Dict, new_content: str) -> Optional[Dict]:
        """Generate a context-aware modification for a specific section."""
        pattern = section['pattern']
        
        # Extract section from new content
        match = re.search(f"({pattern}.*?)(?=\\n(?:def|class|function|interface)\\s|\\Z)", 
                         new_content, re.MULTILINE | re.DOTALL)
        
        if match:
            return {
                'pattern': pattern,
                'replacement': match.group(1),
                'section_name': section['name']
            }
        
        return None
    
    def _try_targeted_edit(self, filepath: str, optimized: Dict) -> Dict:
        """Try to apply a targeted edit."""
        try:
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Replace using pattern
            new_content = re.sub(
                optimized['pattern'] + r'.*?\n(?=\n(?:def|class|function|interface)\s|\Z)',
                optimized['replacement'],
                content,
                flags=re.MULTILINE | re.DOTALL
            )
            
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(new_content)
            
            return {'success': True}
        except Exception as e:
            return {'success': False, 'error': str(e)}
    
    # ============================================================================
    # STRATEGY 5: Script-Assisted Modification
    # ============================================================================
    
    def _execute_script_assist(self, operation: str, filepath: str, new_content: Optional[str]) -> Dict:
        """
        Execute script-assisted modification strategy.
        
        Generates an automation script for user review and execution.
        """
        script = self._generate_automation_script(filepath, new_content)
        
        if not script:
            return {'success': False, 'error': 'Failed to generate automation script'}
        
        logger.info("Generated automation script")
        print("\n" + "="*60)
        print("AUTOMATION SCRIPT GENERATED")
        print("="*60)
        print(script[:500] + "..." if len(script) > 500 else script)
        print("="*60 + "\n")
        
        # Save script to file
        script_path = f"{filepath}.auto_script.py"
        try:
            with open(script_path, 'w') as f:
                f.write(script)
            logger.info(f"Script saved to: {script_path}")
        except Exception as e:
            logger.error(f"Failed to save script: {e}")
        
        # Check if auto-execution is enabled
        auto_execute = self.config.get('strategies', {}).get('script_assist', {}).get('auto_execute', False)
        
        if auto_execute:
            logger.info("Auto-execution enabled, executing script")
            return self._execute_script(script, filepath)
        else:
            return {
                'success': False,
                'error': 'Script generated but not executed (auto_execute disabled)',
                'strategy': 'script_assist',
                'script_path': script_path,
                'script_preview': script[:200] + "..."
            }
    
    def _generate_automation_script(self, filepath: str, new_content: Optional[str]) -> str:
        """Generate an automation script for file modification."""
        timestamp = datetime.now().isoformat()
        original_content = self.state['original_content'].replace('"', '\\"').replace("'", "\\'")
        new_content_escaped = new_content.replace('"', '\\"').replace("'", "\\'") if new_content else ""
        
        script = f'''#!/usr/bin/env python3
"""
Auto-generated modification script
File: {filepath}
Generated: {timestamp}
"""

import sys
import os
import shutil
from pathlib import Path

def apply_changes():
    filepath = "{filepath}"
    backup_path = "{filepath}.auto_backup"
    
    # Create backup if file exists
    if os.path.exists(filepath):
        shutil.copy2(filepath, backup_path)
        print(f"Backup created: {{backup_path}}")
    
    # Apply changes
    new_content = """
{new_content_escaped}
"""
    
    try:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print("Changes applied successfully!")
        return True
    except Exception as e:
        print(f"Error applying changes: {{e}}")
        # Restore backup
        if os.path.exists(backup_path):
            shutil.copy2(backup_path, filepath)
            print("Restored from backup")
        return False

if __name__ == "__main__":
    if apply_changes():
        sys.exit(0)
    else:
        sys.exit(1)
'''
        return script
    
    def _execute_script(self, script: str, filepath: str) -> Dict:
        """Execute the generated automation script."""
        try:
            # Save and execute
            script_path = f"{filepath}.temp_script.py"
            with open(script_path, 'w') as f:
                f.write(script)
            
            import subprocess
            result = subprocess.run(
                [sys.executable, script_path],
                capture_output=True,
                text=True,
                timeout=60
            )
            
            # Clean up
            if os.path.exists(script_path):
                os.remove(script_path)
            
            if result.returncode == 0:
                return {
                    'success': True,
                    'strategy': 'script_assist',
                    'output': result.stdout
                }
            else:
                return {
                    'success': False,
                    'error': f"Script execution failed: {result.stderr}",
                    'strategy': 'script_assist'
                }
        except Exception as e:
            return {
                'success': False,
                'error': f"Script execution error: {str(e)}",
                'strategy': 'script_assist'
            }
    
    # ============================================================================
    # Final Fallback
    # ============================================================================
    
    def _final_fallback(self, original_content: str, new_content: Optional[str], filepath: str) -> Dict:
        """
        Final fallback when all strategies fail.
        
        Generates diff report and manual modification guide.
        """
        logger.error("All strategies failed. Generating manual fallback.")
        
        # Generate diff report
        diff_report = self._generate_diff_report(original_content, new_content)
        
        # Generate manual guide
        manual_guide = self._generate_manual_modification_guide(original_content, new_content)
        
        print("\n" + "="*60)
        print("MANUAL MODIFICATION REQUIRED")
        print("="*60)
        print("\nDiff Report (first 100 lines):")
        print(diff_report[:2000])
        print("\n" + "="*60)
        print(manual_guide)
        print("="*60 + "\n")
        
        # Save reports to file
        report_path = f"{filepath}.manual_modification_report.txt"
        try:
            with open(report_path, 'w', encoding='utf-8') as f:
                f.write("MANUAL MODIFICATION REPORT\\n")
                f.write("="*60 + "\\n\\n")
                f.write("DIFF REPORT:\\n")
                f.write(diff_report)
                f.write("\\n\\n")
                f.write("MANUAL GUIDE:\\n")
                f.write(manual_guide)
            logger.info(f"Report saved to: {report_path}")
        except Exception as e:
            logger.error(f"Failed to save report: {e}")
        
        return {
            'success': False,
            'error': 'All strategies failed. Manual modification required.',
            'action': 'manual_modification_required',
            'diff_report': diff_report,
            'manual_guide': manual_guide,
            'report_path': report_path,
            'failed_strategies': self.state['failed_operations']
        }
    
    def _generate_diff_report(self, original: str, new: Optional[str]) -> str:
        """Generate a unified diff report."""
        if not original or not new:
            return "Cannot generate diff: missing content"
        
        original_lines = original.split('\n')
        new_lines = new.split('\n')
        
        diff = difflib.unified_diff(
            original_lines,
            new_lines,
            fromfile='original',
            tofile='modified',
            lineterm='',
            n=3
        )
        
        return '\n'.join(list(diff)[:100])  # First 100 lines
    
    def _generate_manual_modification_guide(self, original: str, new: Optional[str]) -> str:
        """Generate a manual modification guide."""
        guide = """
# MANUAL MODIFICATION GUIDE

## Steps:
1. Create a backup of the original file
2. Use a diff tool to compare original and target content
3. Apply changes in small increments:
   - Start with independent functions/classes
   - Then modify dependent sections
   - Finally update global configurations

## Recommended Tools:
- VS Code: Built-in diff viewer
- Beyond Compare: Professional comparison tool
- Meld: Open source visual diff
- vimdiff: Command line diff

## Safety Tips:
- Test after each modification
- Use version control (git)
- Consider splitting into smaller files

## Alternative Approach:
If direct modification is too risky:
1. Create a new file with the target content
2. Test thoroughly
3. Replace the original file
4. Maintain the backup until verified
"""
        return guide
    
    def _rollback_to_backup(self, filepath: str):
        """Rollback to backup if available."""
        if self.state['backup_path'] and os.path.exists(self.state['backup_path']):
            try:
                shutil.copy2(self.state['backup_path'], filepath)
                logger.info(f"Rolled back to backup: {self.state['backup_path']}")
            except Exception as e:
                logger.error(f"Rollback failed: {e}")


class PerformanceMonitor:
    """Monitor and report on strategy performance."""
    
    @staticmethod
    def generate_report(results: List[Dict]) -> str:
        """Generate a performance report from strategy results."""
        if not results:
            return "No results to report"
        
        total = len(results)
        successes = len([r for r in results if r.get('success')])
        failures = total - successes
        
        report_lines = [
            "Large File Handler Performance Report",
            "=" * 40,
            f"Total attempts: {total}",
            f"Successes: {successes}",
            f"Failures: {failures}",
            f"Success rate: {(successes/total)*100:.1f}%",
            "",
            "Strategy Success Rates:"
        ]
        
        # Group by strategy
        strategies = {}
        for result in results:
            strategy = result.get('strategy', 'unknown')
            if strategy not in strategies:
                strategies[strategy] = {'success': 0, 'total': 0}
            
            strategies[strategy]['total'] += 1
            if result.get('success'):
                strategies[strategy]['success'] += 1
        
        for strategy, stats in sorted(strategies.items()):
            rate = (stats['success'] / stats['total']) * 100
            report_lines.append(
                f"  {strategy}: {rate:.1f}% ({stats['success']}/{stats['total']})"
            )
        
        return '\n'.join(report_lines)


# =============================================================================
# CLI Interface
# =============================================================================

def create_parser() -> argparse.ArgumentParser:
    """Create CLI argument parser."""
    parser = argparse.ArgumentParser(
        description='Large File Handler - Intelligent graceful degradation for file operations'
    )
    
    subparsers = parser.add_subparsers(dest='command', help='Available commands')
    
    # Edit command
    edit_parser = subparsers.add_parser('edit', help='Edit a file with graceful degradation')
    edit_parser.add_argument('--file', '-f', required=True, help='Path to file to edit')
    edit_parser.add_argument('--new-content', '-n', help='Path to file containing new content')
    edit_parser.add_argument('--content', '-c', help='New content as string')
    edit_parser.add_argument('--config', help='Path to configuration file')
    
    # Analyze command
    analyze_parser = subparsers.add_parser('analyze', help='Analyze a file')
    analyze_parser.add_argument('--file', '-f', required=True, help='Path to file to analyze')
    
    # Stats command
    stats_parser = subparsers.add_parser('stats', help='Show performance statistics')
    stats_parser.add_argument('--results-file', help='Path to results JSON file')
    
    return parser


def main():
    """Main CLI entry point."""
    parser = create_parser()
    args = parser.parse_args()
    
    if not args.command:
        parser.print_help()
        return
    
    if args.command == 'edit':
        # Get new content
        new_content = None
        if args.new_content:
            with open(args.new_content, 'r', encoding='utf-8') as f:
                new_content = f.read()
        elif args.content:
            new_content = args.content
        else:
            print("Error: Must provide --new-content or --content")
            return
        
        # Execute with graceful degradation
        controller = LargeFileController(config_path=args.config)
        result = controller.execute_with_graceful_degradation(
            operation='edit',
            filepath=args.file,
            new_content=new_content
        )
        
        # Print result
        print("\n" + "="*60)
        if result['success']:
            print(f"SUCCESS: Strategy used - {result.get('strategy', 'unknown')}")
        else:
            print(f"FAILED: {result.get('error', 'Unknown error')}")
            if 'manual_guide' in result:
                print("\nManual modification guide generated.")
        print("="*60)
        
        # Save result
        result_path = f"{args.file}.result.json"
        with open(result_path, 'w') as f:
            json.dump(result, f, indent=2, default=str)
        print(f"Result saved to: {result_path}")
    
    elif args.command == 'analyze':
        # Analyze file
        if not os.path.exists(args.file):
            print(f"Error: File not found: {args.file}")
            return
        
        with open(args.file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        handler = LargeFileHandler()
        stats = handler.get_file_stats(content)
        
        print(f"\nFile Analysis: {args.file}")
        print("="*60)
        print(f"Total lines: {stats['total_lines']}")
        print(f"Non-empty lines: {stats['non_empty_lines']}")
        print(f"Total characters: {stats['total_chars']}")
        print(f"Estimated tokens: {stats['estimated_tokens']}")
        print(f"Large file threshold: {handler.max_lines} lines")
        print(f"Would activate degradation: {stats['total_lines'] > handler.max_lines}")
        print("="*60)
    
    elif args.command == 'stats':
        # Show statistics
        if args.results_file and os.path.exists(args.results_file):
            with open(args.results_file, 'r') as f:
                results = json.load(f)
            report = PerformanceMonitor.generate_report(results)
            print(report)
        else:
            print("No results file provided or file not found")
            print("Performance statistics require a results JSON file")


if __name__ == "__main__":
    main()
