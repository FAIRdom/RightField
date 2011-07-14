package uk.ac.manchester.cs.owl.semspreadsheets.ui.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import uk.ac.manchester.cs.owl.semspreadsheets.ui.ProgressDialog;
import uk.ac.manchester.cs.owl.semspreadsheets.ui.WorkbookFrame;

/**
 * Author: Matthew Horridge, Stuart Owen<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 03-Feb-2010
 * 
 * @author Matthew Horridge
 * @author Stuart Owen
 */
public class TaskManager implements TaskListener {

    public static final long TIME_UNTIL_PROGRESS = 1;
    
    private static final Logger logger = Logger.getLogger(TaskManager.class);

    private WorkbookFrame workbookFrame;

    private ProgressDialog dlg;

    private ExecutorService executor;

    private int nestingLevel = 0;

    public TaskManager(WorkbookFrame workbookFrame) {
        this.workbookFrame = workbookFrame;
        executor = Executors.newSingleThreadExecutor();
        dlg = new ProgressDialog(workbookFrame);
        dlg.pack();
    }

    /**
     * Asks for a task to be run. The task will be run from a thread other than the event dispatch thread
     * @param callable The task that will be run
     * @return The return value of the task
     * @throws E from the task
     */
    public <V, E extends Exception> V runTask(Task<V, E> callable) throws E {
        try {
            V returnValue = null;
            CallableTask<V, E> callableTask = new CallableTask<V, E>(callable);
            if (nestingLevel == 0 || SwingUtilities.isEventDispatchThread()) {
                final Future<V> future = executor.submit(callableTask);
                try {
                    // Get the return value after the timeout.  If the task is still running
                    // a timeout exception will be thrown, in which case we display the dialog
                    // and then wait again for the task to finish.                            	
                    returnValue = future.get(TIME_UNTIL_PROGRESS, TimeUnit.MILLISECONDS);
                }
                catch (InterruptedException e) {
                    logger.error("Interuption Error",e);
                }
                catch (ExecutionException e) {
                    // Early termination - an exception was thrown from within the task.  We get rid of the dialog
                    // and rethrow the exception.
                    hideDialog();
                    // Rethrow exception                    
                    logger.error("Task execution error",e);
                    if(e.getCause() instanceof RuntimeException) {
                        throw (RuntimeException) e.getCause();
                    }
                    else {
                        throw (E) e.getCause();
                    }

                }
                catch (TimeoutException e) {
                    // Time to show the progress dialog.  Since this is the event dispatch thread,
                    // we will block by showing the dialog because it is modal.
                    // The dialog will be hidden when the root task completes
                    dlg.setVisible(true);
                    // Task done
                    returnValue = future.get();
                }
            }
            else {            	
                returnValue = callableTask.call();
            }
            return returnValue;
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        catch (ExecutionException e) {        	
        	if(e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            else {
                throw (E) e.getCause();
            }
        }
    }

    public void messageChanged(final Task task) {
        if (SwingUtilities.isEventDispatchThread()) {
            dlg.setTitle(task.getTitle());
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    dlg.setTitle(task.getTitle());
                }
            });
        }
    }

    public void lengthChanged(final Task task) {
        if (SwingUtilities.isEventDispatchThread()) {
            dlg.setLength(task.getLength());
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    dlg.setLength(task.getLength());
                }
            });
        }
    }

    public void progressChanged(final Task task) {
        Runnable runnable = new Runnable() {
            public void run() {
                int progress = task.getProgress();
                if (progress > -1) {
                    dlg.setProgressIndeterminate(false);
                    dlg.setProgress(progress);
                }
                else {
                    dlg.setProgressIndeterminate(true);
                }
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        }
        else {
                SwingUtilities.invokeLater(runnable);
        }
    }


    /**
     * Hides the progress dialog (from the EDT) if it is visible.
     */
    private void hideDialog() {
        if (!dlg.isVisible()) {
            return;
        }
        Runnable runnable = new Runnable() {
            public void run() {
                dlg.setVisible(false);
            }
        };
        SwingUtilities.invokeLater(runnable);
    }


    /**
     * A wrapper for a workbookManager task so that it can be run by the execution service
     */
    private class CallableTask<V, E extends Exception> implements Callable<V> {

        private Task<V, E> task;

        public CallableTask(Task<V, E> task) {
            this.task = task;
        }

        /**
         * Computes a result, or throws an exception if unable to do so.
         * @return computed result
         * @throws E if unable to compute a result
         */
        public V call() throws E {
            nestingLevel++;
            task.setup(workbookFrame);
            if (nestingLevel == 1) {
                task.addTaskListener(TaskManager.this);
                messageChanged(task);
                dlg.setTask(task);
            }
            V retVal;
            try {
                retVal = task.runTask();
            }
            finally {
                task.removeTaskListener(TaskManager.this);
                // Hide the dialog if we have finished.
                nestingLevel--;
                if(nestingLevel == 0) {
                    hideDialog();
                }
            }
            return retVal;
        }
    }

    private static int count;

    public static void main(String[] args) {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        Thread starter = new Thread(new Runnable() {
            public void run() {
                for(int i = 0; i < 10; i++) {
                    logger.debug("Schduling " + count);
                    executor.execute(new Runnable() {


                        public void run() {
                            count++;
                            logger.debug("Thread " + count + " starting");
                            try {
                                Thread.sleep(500);
                            }
                            catch (InterruptedException e) {
                                logger.error("Thread Interruption Error",e);
                            }
                            logger.debug("\tThread " + count + " finished");
                        }
                    });
            }

        }
        });
        starter.start();

    }
}

