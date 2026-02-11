#!/usr/bin/env python3
"""
Test suite for Large File Handler skill

This test suite validates the functionality of the large_file_handler module,
ensuring all strategies work correctly.
"""

import os
import sys
import unittest
import tempfile
import shutil
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent / 'scripts'))

from large_file_handler import LargeFileHandler, LargeFileController, PerformanceMonitor


class TestLargeFileHandler(unittest.TestCase):
    """Test cases for LargeFileHandler class."""
    
    def setUp(self):
        self.handler = LargeFileHandler(max_lines=100)
    
    def test_should_activate_large_file(self):
        """Test that large files trigger activation."""
        content = "\n".join([f"line {i}" for i in range(150)])
        self.assertTrue(self.handler.should_activate(content, 'edit'))
        self.assertTrue(self.handler.should_activate(content, 'write'))
        self.assertTrue(self.handler.should_activate(content, 'rewrite'))
    
    def test_should_not_activate_small_file(self):
        """Test that small files don't trigger activation."""
        content = "\n".join([f"line {i}" for i in range(50)])
        self.assertFalse(self.handler.should_activate(content, 'edit'))
    
    def test_should_not_activate_low_risk_operation(self):
        """Test that low-risk operations don't trigger activation."""
        content = "\n".join([f"line {i}" for i in range(150)])
        self.assertFalse(self.handler.should_activate(content, 'read'))
        self.assertFalse(self.handler.should_activate(content, 'analyze'))
    
    def test_get_file_stats(self):
        """Test file statistics calculation."""
        lines = [f"line {i}" for i in range(50)]
        content = "\n".join(lines)
        
        stats = self.handler.get_file_stats(content)
        
        self.assertEqual(stats['total_lines'], 50)
        self.assertEqual(stats['non_empty_lines'], 50)
        self.assertEqual(stats['total_chars'], len(content))
        self.assertGreater(stats['estimated_tokens'], 0)


class TestLargeFileController(unittest.TestCase):
    """Test cases for LargeFileController class."""
    
    def setUp(self):
        self.controller = LargeFileController()
        self.temp_dir = tempfile.mkdtemp()
    
    def tearDown(self):
        shutil.rmtree(self.temp_dir, ignore_errors=True)
    
    def test_execute_with_small_file(self):
        """Test direct execution with small files."""
        test_file = os.path.join(self.temp_dir, 'small_file.txt')
        content = "Small file content\n" * 10
        new_content = "Modified content\n" * 10
        
        with open(test_file, 'w') as f:
            f.write(content)
        
        result = self.controller.execute_with_graceful_degradation(
            operation='edit',
            filepath=test_file,
            new_content=new_content
        )
        
        self.assertTrue(result['success'])
        
        with open(test_file, 'r') as f:
            self.assertEqual(f.read(), new_content)
    
    def test_execute_with_large_file(self):
        """Test execution with large files."""
        test_file = os.path.join(self.temp_dir, 'large_file.txt')
        content = "Large file content\n" * 600
        new_content = "Modified content\n" * 600
        
        with open(test_file, 'w') as f:
            f.write(content)
        
        result = self.controller.execute_with_graceful_degradation(
            operation='edit',
            filepath=test_file,
            new_content=new_content
        )
        
        self.assertTrue(result['success'])
        self.assertIn('strategy', result)
    
    def test_four_step_strategy(self):
        """Test four-step anti-loop strategy."""
        test_file = os.path.join(self.temp_dir, 'four_step_test.txt')
        content = "Test content\n" * 200
        new_content = "Modified content\n" * 200
        
        with open(test_file, 'w') as f:
            f.write(content)
        
        self.controller.state['original_content'] = content
        result = self.controller._execute_four_step_strategy('edit', test_file, new_content)
        
        self.assertTrue(result['success'])
        self.assertEqual(result['strategy'], 'four_step_anti_loop')
        self.assertIn('results', result)
    
    def test_chunk_processing_strategy(self):
        """Test chunk processing strategy."""
        test_file = os.path.join(self.temp_dir, 'chunk_test.txt')
        
        # Create large content (>200 lines difference)
        content = "Original line\n" * 100
        new_content = "Modified line\n" * 500  # Large change
        
        with open(test_file, 'w') as f:
            f.write(content)
        
        self.controller.state['original_content'] = content
        result = self.controller._execute_chunk_processing('edit', test_file, new_content)
        
        self.assertTrue(result['success'])
        # Should use chunk_processing due to large diff
        self.assertEqual(result['strategy'], 'chunk_processing')
        self.assertIn('chunks_processed', result)
    
    def test_incremental_edit_strategy(self):
        """Test incremental editing strategy."""
        test_file = os.path.join(self.temp_dir, 'incremental_test.txt')
        content = "Line 1\nLine 2\nLine 3\n"
        new_content = "Line 1\nModified Line 2\nLine 3\nNew Line 4\n"
        
        with open(test_file, 'w') as f:
            f.write(content)
        
        self.controller.state['original_content'] = content
        result = self.controller._execute_incremental_edit('edit', test_file, new_content)
        
        self.assertTrue(result['success'])
        self.assertEqual(result['strategy'], 'incremental_edit')
    
    def test_backup_creation(self):
        """Test that backups are created."""
        test_file = os.path.join(self.temp_dir, 'backup_test.txt')
        content = "Original content\n" * 50
        new_content = "Modified content\n" * 50
        
        with open(test_file, 'w') as f:
            f.write(content)
        
        self.controller.state['original_content'] = content
        self.controller._preparation_phase(test_file, new_content)
        
        self.assertIsNotNone(self.controller.state['backup_path'])
        self.assertTrue(os.path.exists(self.controller.state['backup_path']))


class TestPerformanceMonitor(unittest.TestCase):
    """Test cases for PerformanceMonitor class."""
    
    def test_generate_report(self):
        """Test performance report generation."""
        results = [
            {'success': True, 'strategy': 'four_step_anti_loop'},
            {'success': True, 'strategy': 'four_step_anti_loop'},
            {'success': False, 'strategy': 'chunk_processing', 'error': 'Failed'},
        ]
        
        report = PerformanceMonitor.generate_report(results)
        
        self.assertIn('Total attempts: 3', report)
        self.assertIn('Successes: 2', report)
        self.assertIn('Failures: 1', report)
        self.assertIn('four_step_anti_loop', report)
        self.assertIn('chunk_processing', report)
    
    def test_generate_report_empty(self):
        """Test report generation with empty results."""
        report = PerformanceMonitor.generate_report([])
        self.assertIn('No results to report', report)


class TestIntegration(unittest.TestCase):
    """Integration tests for the complete workflow."""
    
    def setUp(self):
        self.temp_dir = tempfile.mkdtemp()
    
    def tearDown(self):
        shutil.rmtree(self.temp_dir, ignore_errors=True)
    
    def test_full_workflow_small_file(self):
        """Test complete workflow with a small file."""
        test_file = os.path.join(self.temp_dir, 'integration_small.txt')
        content = "Small file\n" * 50
        new_content = "Modified\n" * 50
        
        with open(test_file, 'w') as f:
            f.write(content)
        
        controller = LargeFileController()
        result = controller.execute_with_graceful_degradation(
            operation='edit',
            filepath=test_file,
            new_content=new_content
        )
        
        self.assertTrue(result['success'])
        
        with open(test_file, 'r') as f:
            self.assertEqual(f.read(), new_content)
    
    def test_full_workflow_large_file(self):
        """Test complete workflow with a large file."""
        test_file = os.path.join(self.temp_dir, 'integration_large.txt')
        content = "Large file content line\n" * 600
        new_content = "Modified large file content\n" * 600
        
        with open(test_file, 'w') as f:
            f.write(content)
        
        controller = LargeFileController()
        result = controller.execute_with_graceful_degradation(
            operation='edit',
            filepath=test_file,
            new_content=new_content
        )
        
        self.assertTrue(result['success'])
        self.assertIn('strategy', result)
        
        with open(test_file, 'r') as f:
            actual_content = f.read()
            self.assertEqual(len(actual_content), len(new_content))


def run_tests():
    """Run all tests and generate report."""
    loader = unittest.TestLoader()
    suite = unittest.TestSuite()
    
    # Add all test classes
    suite.addTests(loader.loadTestsFromTestCase(TestLargeFileHandler))
    suite.addTests(loader.loadTestsFromTestCase(TestLargeFileController))
    suite.addTests(loader.loadTestsFromTestCase(TestPerformanceMonitor))
    suite.addTests(loader.loadTestsFromTestCase(TestIntegration))
    
    runner = unittest.TextTestRunner(verbosity=2)
    result = runner.run(suite)
    
    return result.wasSuccessful()


if __name__ == '__main__':
    success = run_tests()
    sys.exit(0 if success else 1)
