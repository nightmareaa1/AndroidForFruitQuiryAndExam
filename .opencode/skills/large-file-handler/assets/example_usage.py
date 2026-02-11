#!/usr/bin/env python3
"""
Example usage of Large File Handler

This script demonstrates how to use the large_file_handler module
for handling large file operations with graceful degradation.
"""

import os
import sys

# Add the scripts directory to the path
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'scripts'))

from large_file_handler import LargeFileController, LargeFileHandler, PerformanceMonitor


def example_1_basic_usage():
    """Example 1: Basic usage with automatic strategy selection."""
    print("Example 1: Basic Usage")
    print("=" * 60)
    
    # Create a test file with 600 lines
    test_file = "/tmp/test_large_file.py"
    content = "\n".join([f"# Line {i}: This is a test line" for i in range(600)])
    
    with open(test_file, 'w') as f:
        f.write(content)
    
    # Initialize controller
    controller = LargeFileController()
    
    # Create new content (modified version)
    new_content = content.replace("test line", "modified line")
    new_content += "\n# Additional line at the end"
    
    # Check if degradation is needed
    if controller.handler.should_activate(content, 'edit'):
        print(f"File has {len(content.split(chr(10)))} lines - activating graceful degradation")
        
        result = controller.execute_with_graceful_degradation(
            operation='edit',
            filepath=test_file,
            new_content=new_content
        )
        
        print(f"Success: {result['success']}")
        print(f"Strategy used: {result.get('strategy', 'N/A')}")
        
        if not result['success']:
            print(f"Error: {result.get('error', 'Unknown error')}")
    else:
        print("File size within limits - direct edit would be used")
    
    # Cleanup
    if os.path.exists(test_file):
        os.remove(test_file)
    
    print()


def example_2_file_analysis():
    """Example 2: Analyze file without modifying."""
    print("Example 2: File Analysis")
    print("=" * 60)
    
    test_file = "/tmp/test_analysis.py"
    content = "\n".join([f"def function_{i}():" for i in range(300)])
    
    with open(test_file, 'w') as f:
        f.write(content)
    
    with open(test_file, 'r') as f:
        content = f.read()
    
    handler = LargeFileHandler()
    stats = handler.get_file_stats(content)
    
    print(f"File statistics:")
    print(f"  Total lines: {stats['total_lines']}")
    print(f"  Non-empty lines: {stats['non_empty_lines']}")
    print(f"  Total characters: {stats['total_chars']}")
    print(f"  Estimated tokens: {stats['estimated_tokens']}")
    print(f"  Would activate degradation: {stats['total_lines'] > handler.max_lines}")
    
    # Cleanup
    if os.path.exists(test_file):
        os.remove(test_file)
    
    print()


def example_3_chunk_processing():
    """Example 3: Demonstrate chunk processing strategy."""
    print("Example 3: Chunk Processing Strategy")
    print("=" * 60)
    
    test_file = "/tmp/test_chunks.py"
    
    # Create content with 800 lines
    lines = []
    for i in range(20):
        lines.append(f"class Class{i}:")
        lines.append(f"    def method1(self):")
        lines.append(f"        pass")
        lines.append(f"    ")
        lines.append(f"    def method2(self):")
        lines.append(f"        return {i}")
        lines.append("")
    
    content = "\n".join(lines)
    
    with open(test_file, 'w') as f:
        f.write(content)
    
    controller = LargeFileController()
    
    # Modify content
    new_content = content.replace("pass", "return None")
    
    result = controller.execute_with_graceful_degradation(
        operation='edit',
        filepath=test_file,
        new_content=new_content
    )
    
    print(f"Operation result:")
    print(f"  Success: {result['success']}")
    print(f"  Strategy: {result.get('strategy', 'N/A')}")
    
    if 'chunks_processed' in result:
        print(f"  Chunks processed: {result['chunks_processed']}")
    
    # Cleanup
    if os.path.exists(test_file):
        os.remove(test_file)
    
    print()


def example_4_performance_monitoring():
    """Example 4: Generate performance report."""
    print("Example 4: Performance Monitoring")
    print("=" * 60)
    
    # Simulate multiple operation results
    results = [
        {'success': True, 'strategy': 'four_step_anti_loop'},
        {'success': True, 'strategy': 'four_step_anti_loop'},
        {'success': False, 'strategy': 'chunk_processing', 'error': 'Chunk failed'},
        {'success': True, 'strategy': 'incremental_edit'},
        {'success': True, 'strategy': 'context_optimization'},
        {'success': False, 'strategy': 'script_assist', 'error': 'User cancelled'},
    ]
    
    report = PerformanceMonitor.generate_report(results)
    print(report)
    print()


def example_5_cli_usage():
    """Example 5: Command-line interface usage."""
    print("Example 5: CLI Usage")
    print("=" * 60)
    print("""
# Analyze a file
python large_file_handler.py analyze --file large_project.py

# Edit with graceful degradation
python large_file_handler.py edit \\
    --file large_project.py \\
    --new-content modifications.py

# Show statistics from previous runs
python large_file_handler.py stats --results-file results.json
""")
    print()


if __name__ == "__main__":
    print("\n" + "=" * 60)
    print("Large File Handler - Usage Examples")
    print("=" * 60 + "\n")
    
    example_1_basic_usage()
    example_2_file_analysis()
    example_3_chunk_processing()
    example_4_performance_monitoring()
    example_5_cli_usage()
    
    print("=" * 60)
    print("Examples completed!")
    print("=" * 60)
