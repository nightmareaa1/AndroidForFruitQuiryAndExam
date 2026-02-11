# Detailed Strategy Documentation

This document provides detailed information about each degradation strategy implemented in the Large File Handler skill.

## Strategy Overview

The Large File Handler implements a cascading fallback system with 5 levels of strategies:

```
Level 1: Direct Edit (no degradation needed)
    ↓ (if fails)
Level 2: Four-Step Anti-Loop Method
    ↓ (if fails)
Level 3: Chunk Processing
    ↓ (if fails)
Level 4: Incremental Editing
    ↓ (if fails)
Level 5: Context Optimization
    ↓ (if fails)
Level 6: Script-Assisted Modification
    ↓ (if all fail)
Final: Manual Modification Guide
```

## Strategy 1: Four-Step Anti-Loop Method

### Purpose
Prevent infinite loops by breaking down the edit operation into discrete, verifiable steps.

### Steps

#### 1. Analysis Phase
- Examines file structure (classes, functions)
- Calculates change metrics (line counts, diff size)
- Analyzes change pattern (append, delete, modify)
- Provides recommendations for approach

**Output:**
```python
{
    'success': True,
    'structure_preview': [...],  # First 20 structural elements
    'metrics': {
        'original_lines': 600,
        'new_lines': 650,
        'diff_size': 50
    },
    'suggestion': 'Mixed modifications'
}
```

#### 2. Preparation Phase
- Creates backup with timestamp and content hash
- Generates modification plan
- Estimates risk level

**Output:**
```python
{
    'success': True,
    'backup_path': 'file.py.backup_20240210_120000_a1b2c3d4',
    'modification_plan': {
        'file': 'file.py',
        'original_line_count': 600,
        'new_line_count': 650,
        'estimated_chunks': 4,
        'risk_level': 'medium'
    }
}
```

#### 3. Execution Phase
- Writes new content to file
- Tracks bytes and lines written
- Captures any errors

#### 4. Verification Phase
- Reads back the written content
- Verifies content matches expected
- Performs sanity checks

**Checks:**
- File exists
- Not empty
- Proper encoding
- Content matches

### When to Use
- Best for files with 500-1000 lines
- When structure is well-defined
- For controlled, predictable changes

### Limitations
- Requires sufficient memory to hold entire file
- Single-point failure if write fails
- No partial success recovery

## Strategy 2: Chunk Processing

### Purpose
Break large files into smaller, manageable chunks to reduce per-operation complexity.

### How It Works

1. **Calculate Change Scope**
   - Generate unified diff
   - Count changed lines
   - Skip if changes are small enough

2. **Split into Chunks**
   - Default chunk size: 200 lines
   - Find logical boundaries (empty lines, class/function boundaries)
   - Create chunk metadata

3. **Process Sequentially**
   - Write first chunk fresh
   - Append subsequent chunks
   - Validate after each chunk

4. **Rollback on Failure**
   - Restore from backup if any chunk fails
   - Preserve partial progress information

### Chunk Metadata
```python
{
    'start_line': 1,
    'end_line': 200,
    'content': '...',
    'is_first': True,
    'is_last': False
}
```

### When to Use
- Files with >1000 lines
- When memory is constrained
- For append-heavy modifications

### Limitations
- Overhead from multiple I/O operations
- Risk of partial file corruption
- Complex rollback logic

## Strategy 3: Incremental Editing

### Purpose
Apply changes as discrete operations (additions, deletions) rather than full rewrite.

### How It Works

1. **Generate Diff**
   - Use unified diff format
   - Capture all changes

2. **Parse into Operations**
   - Convert diff to operation list
   - Types: `add`, `delete`
   - Track line numbers

**Operation Format:**
```python
{
    'type': 'add',  # or 'delete'
    'line': 42,
    'content': 'def new_function():'
}
```

3. **Apply Incrementally**
   - Process one operation at a time
   - Update line numbers after each operation
   - Validate file integrity

4. **Track Progress**
   - Record successful operations
   - Enable resume from failure point

### When to Use
- Precision is required
- Changes are localized
- Memory is limited

### Limitations
- Slower than bulk operations
- Line number tracking complexity
- Potential for cascading failures

## Strategy 4: Context Optimization

### Purpose
Reduce context window usage by focusing only on relevant sections.

### How It Works

1. **Identify Relevant Sections**
   - Parse modified code for function/class names
   - Match against original file
   - Create section map

**Section Format:**
```python
{
    'type': 'def',
    'name': 'process_data',
    'pattern': r'^def\s+process_data'
}
```

2. **Generate Context-Aware Modifications**
   - Extract section from new content
   - Create targeted edit
   - Preserve surrounding context

3. **Apply Targeted Edits**
   - Use regex-based replacement
   - Maintain file structure
   - Validate syntax

### When to Use
- Only specific functions/classes changed
- Context window is limited
- Need to preserve formatting

### Limitations
- Requires well-structured code
- Regex may fail on complex patterns
- Doesn't handle global changes well

## Strategy 5: Script-Assisted Modification

### Purpose
Generate executable Python scripts for user review and controlled execution.

### How It Works

1. **Generate Automation Script**
   - Creates standalone Python script
   - Includes backup and restore logic
   - Provides execution trace

**Script Features:**
- Automatic backup creation
- Error handling
- Rollback on failure
- Progress reporting

2. **User Review**
   - Displays script preview
   - Saves to file for inspection
   - Waits for user confirmation

3. **Execution Options**
   - Manual: User runs script separately
   - Auto: Execute immediately (configurable)

### When to Use
- Maximum control required
- Complex transformations
- Audit trail needed

### Limitations
- Requires user interaction
- Script generation overhead
- Security considerations

## Final Fallback: Manual Modification Guide

### Purpose
When all automated strategies fail, provide human-actionable guidance.

### Components

1. **Diff Report**
   - Unified diff (first 100 lines)
   - Context lines for each change
   - Statistics

2. **Manual Guide**
   - Step-by-step instructions
   - Tool recommendations
   - Safety tips

3. **Failure Analysis**
   - List of failed strategies
   - Error messages
   - Suggested next steps

### Output Format
```
MANUAL MODIFICATION REQUIRED
================================

Diff Report:
--- original
+++ modified
@@ -10,5 +10,5 @@
-    old_code()
+    new_code()

Manual Guide:
1. Create backup
2. Use diff tool
3. Apply changes incrementally
...
```

## Strategy Selection Guidelines

### Quick Reference

| File Size | Change Type | Recommended Strategy |
|-----------|-------------|---------------------|
| 500-1000 lines | Any | Four-Step Anti-Loop |
| 1000-2000 lines | Append-heavy | Chunk Processing |
| 1000-2000 lines | Mixed | Incremental Edit |
| 2000+ lines | Localized | Context Optimization |
| Any | Complex/Unknown | Script-Assisted |

### Performance Considerations

**Speed (fastest to slowest):**
1. Direct Edit
2. Four-Step Anti-Loop
3. Chunk Processing
4. Incremental Edit
5. Context Optimization
6. Script-Assisted

**Reliability (most to least):**
1. Script-Assisted (user-controlled)
2. Four-Step Anti-Loop
3. Context Optimization
4. Incremental Edit
5. Chunk Processing
6. Direct Edit

**Memory Usage (least to most):**
1. Incremental Edit
2. Context Optimization
3. Chunk Processing
4. Four-Step Anti-Loop
5. Script-Assisted
6. Direct Edit

## Configuration

### Strategy Tuning

```yaml
strategies:
  chunk_processing:
    chunk_size: 200  # Lines per chunk
    overlap_lines: 10  # Context overlap
    
  incremental_edit:
    max_operations_per_batch: 50  # Batch size
    
  context_optimization:
    context_lines: 50  # Context around changes
```

### Enabling/Disabling Strategies

```yaml
strategies:
  four_step_anti_loop:
    enabled: true
  chunk_processing:
    enabled: false  # Skip this strategy
```

## Monitoring and Optimization

### Performance Metrics

Track these metrics to optimize strategy selection:
- Success rate per strategy
- Average execution time
- Memory usage
- Rollback frequency

### Adaptive Strategy Selection

Based on historical performance, automatically select the best strategy:

```python
# Pseudo-code for adaptive selection
strategy_scores = {
    'four_step_anti_loop': 0.85,
    'chunk_processing': 0.70,
    'incremental_edit': 0.90,
    # ...
}

# Sort by score
sorted_strategies = sorted(
    strategy_scores.items(),
    key=lambda x: x[1],
    reverse=True
)
```

## Best Practices

1. **Always backup** - Enable backup creation in all strategies
2. **Test on small files** - Validate strategies on non-critical files first
3. **Monitor metrics** - Track which strategies work best for your use case
4. **Adjust thresholds** - Tune max_lines based on your environment
5. **Enable logging** - Use detailed logging for troubleshooting

## Troubleshooting

### Common Issues

**Issue:** Chunk processing creates too many chunks
**Solution:** Increase chunk_size in configuration

**Issue:** Incremental edit is too slow
**Solution:** Increase max_operations_per_batch or use chunk processing

**Issue:** Context optimization misses changes
**Solution:** Increase context_lines or use four-step method

**Issue:** Script-assisted requires too much interaction
**Solution:** Enable auto_execute (use with caution)

## Future Enhancements

Potential improvements to consider:
1. Parallel chunk processing
2. Machine learning-based strategy selection
3. Integration with version control systems
4. Real-time collaboration support
5. IDE plugin integration
