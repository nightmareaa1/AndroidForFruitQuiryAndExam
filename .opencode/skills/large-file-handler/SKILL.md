---
name: large-file-handler
description: Intelligent graceful degradation strategy for handling large file edits. Use when editing or writing files exceeds 500 lines to avoid edit/write dead loops. Automatically activates multi-level fallback strategies including four-step anti-loop, chunk processing, incremental editing, context optimization, and script-assisted modification.
---

# Large File Handler

Intelligent graceful degradation strategy for handling large file operations (>500 lines) in AI-assisted coding environments.

## Overview

When AI tools attempt to edit or rewrite large files (>500 lines), they often encounter:
- **Edit failures** due to context window limitations
- **Infinite loops** from repeated failed attempts  
- **Data loss** from partial writes
- **Token exhaustion** from retry storms

This skill provides a **5-level fallback strategy** that automatically activates when large file operations are detected, ensuring reliable modifications with minimal user intervention.

## When to Use This Skill

**Triggers:**
- Editing/writing files with >500 lines of code
- Repeated edit failures on the same file
- Context window exhaustion during file operations
- Complex refactoring operations on large files

**Use Cases:**
1. Large configuration files (YAML, JSON, XML)
2. Generated code files (protobuf, GraphQL schemas)
3. Legacy monolithic files
4. Auto-generated migration scripts
5. Bundle/index files

## Quick Start

### Basic Usage

```python
from large_file_handler import LargeFileController

# Initialize controller
controller = LargeFileController()

# Check if file needs degradation
if controller.handler.should_activate(content, 'edit'):
    result = controller.execute_with_graceful_degradation(
        operation='edit',
        filepath='large_file.py',
        new_content=new_content
    )
    
    if result['success']:
        print(f"Modified using strategy: {result['strategy']}")
    else:
        print("All strategies failed. Manual intervention needed.")
```

### Command Line Usage

```bash
# Edit a large file
python large_file_handler.py edit --file large_project.py --new-content modifications.py

# Analyze file without modifying
python large_file_handler.py analyze --file large_project.py
```

## Degradation Strategies

This skill implements **5 progressive fallback strategies**:

### Level 1: Four-Step Anti-Loop Method
A structured approach to avoid infinite retry loops:
1. **Analysis Phase** - Analyze file structure and change scope
2. **Preparation Phase** - Create backup and modification plan
3. **Execution Phase** - Apply changes with validation
4. **Verification Phase** - Confirm modifications and integrity

### Level 2: Chunk Processing
Split large modifications into manageable chunks (~200 lines):
- Identifies logical boundaries (class/function boundaries)
- Processes chunks sequentially
- Rolls back on any chunk failure
- Maintains context between chunks

### Level 3: Incremental Editing
Apply changes as a series of small, reversible operations:
- Converts diff to discrete operations
- Applies one operation at a time
- Validates after each operation
- Enables partial success scenarios

### Level 4: Context Optimization
Reduce context to only relevant sections:
- Identifies sections requiring modification
- Generates context-aware edits
- Targets specific functions/classes
- Minimizes token usage

### Level 5: Script-Assisted Modification
Generate and execute automation scripts:
- Creates Python script for modifications
- User reviews before execution
- Provides execution trace
- Includes automatic rollback

### Final Fallback: Manual Guide
If all strategies fail, generates:
- **Diff report** - Unified diff of proposed changes
- **Manual guide** - Step-by-step manual modification instructions
- **Tool recommendations** - Suggested diff/merge tools

## Configuration

### YAML Configuration

```yaml
# large_file_config.yaml
large_file_handler:
  max_lines: 500
  max_retries: 2
  strategies:
    - name: four_step_anti_loop
      enabled: true
      timeout: 30
    - name: chunk_processing
      enabled: true
      chunk_size: 200
      overlap_lines: 10
    - name: incremental_edit
      enabled: true
      max_operations_per_batch: 50
    - name: context_optimization
      enabled: true
      context_lines: 50
    - name: script_assist
      enabled: true
      auto_execute: false
  
  fallback:
    show_diff: true
    suggest_tools: true
    generate_guide: true
  
  logging:
    level: "INFO"
    log_file: "large_file_handler.log"
    report_errors: true
```

### Environment Variables

```bash
export LARGE_FILE_MAX_LINES=500
export LARGE_FILE_CHUNK_SIZE=200
export LARGE_FILE_LOG_LEVEL=INFO
export LARGE_FILE_AUTO_EXECUTE=false
```

## API Reference

### LargeFileHandler

Main detection and configuration class.

```python
class LargeFileHandler:
    def __init__(self, max_lines: int = 500, max_retries: int = 2)
    def should_activate(self, content: str, operation: str) -> bool
```

### LargeFileController

Main execution controller with fallback strategies.

```python
class LargeFileController:
    def execute_with_graceful_degradation(
        self, 
        operation: str, 
        filepath: str,
        new_content: str = None
    ) -> Dict
```

### PerformanceMonitor

Monitoring and reporting capabilities.

```python
class PerformanceMonitor:
    @staticmethod
    def generate_report(results: List[Dict]) -> str
```

## Integration Examples

### Integration with AI Tools

```python
def smart_edit_with_fallback(filepath: str, 
                            instruction: str,
                            new_content: str = None,
                            ai_client=None) -> Dict:
    """
    Smart edit function with graceful degradation
    """
    controller = LargeFileController()
    
    # Step 1: Try direct AI edit
    if ai_client:
        try:
            result = ai_client.edit_file(filepath, instruction)
            if result['success']:
                return result
        except Exception as e:
            print(f"Direct edit failed: {e}")
    
    # Step 2: Use degradation strategies
    if new_content:
        return controller.execute_with_graceful_degradation(
            operation='edit',
            filepath=filepath,
            new_content=new_content
        )
    
    return {'success': False, 'error': 'Unable to execute edit'}
```

### Integration with Claude Code

When Claude Code detects a large file operation:

```python
# In Claude Code's edit handler
from large_file_handler import LargeFileController

controller = LargeFileController()
original_content = read_file(filepath)

if controller.handler.should_activate(original_content, operation):
    # Activate graceful degradation
    result = controller.execute_with_graceful_degradation(
        operation=operation,
        filepath=filepath,
        new_content=new_content
    )
    
    if not result['success']:
        # Provide manual guide to user
        return result.get('manual_guide', 'Manual modification required')
```

## Error Handling

All strategies follow consistent error handling:

```python
{
    'success': bool,
    'strategy': str,  # Which strategy was used
    'error': str,     # Error message if failed
    'results': [],    # Detailed results from each step
    'backup_path': str,  # Path to backup file
    'manual_guide': str  # Generated guide (if all failed)
}
```

## Monitoring

Track strategy effectiveness:

```python
from large_file_handler import PerformanceMonitor

# Generate performance report
report = PerformanceMonitor.generate_report(all_results)
print(report)

# Output:
# Large File Handler Performance Report
# ========================================
# Total attempts: 15
# Successes: 12
# Failures: 3
# 
# Strategy success rates:
#   four_step_anti_loop: 80.0% (4/5)
#   chunk_processing: 75.0% (3/4)
#   incremental_edit: 100.0% (2/2)
#   context_optimization: 66.7% (2/3)
#   script_assist: 50.0% (1/2)
```

## Best Practices

1. **Always backup** - All strategies create backups automatically
2. **Review scripts** - Level 5 strategy generates scripts for user review
3. **Monitor metrics** - Track which strategies work best for your use case
4. **Adjust thresholds** - Tune `max_lines` based on your environment
5. **Test on non-critical** - Test strategies on non-critical files first

## Resources

### Scripts
- `large_file_handler.py` - Main implementation
- `cli.py` - Command-line interface

### References
- `strategies.md` - Detailed strategy documentation
- `integration_guide.md` - Integration patterns

### Examples
- `example_usage.py` - Usage examples
- `test_scenarios.py` - Test scenarios and validation

## License

MIT License - See LICENSE file for details.
