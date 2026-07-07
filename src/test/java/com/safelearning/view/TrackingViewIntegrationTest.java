package com.safelearning.view;

import com.safelearning.controller.IssueController;
import com.safelearning.service.IssueService;
import com.safelearning.strategy.HazardTypePriorityStrategy;

import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrackingViewIntegrationTest {

    @Test
    void clickBackButton_shouldCloseTrackingView() throws Exception {

        IssueService issueService =
                new IssueService(
                        new HazardTypePriorityStrategy()
                );

        IssueController controller =
                new IssueController(issueService);

        final TrackingView[] viewHolder =
                new TrackingView[1];

        SwingUtilities.invokeAndWait(() -> {
            viewHolder[0] = new TrackingView(controller);
        });

        TrackingView view = viewHolder[0];

        assertTrue(view.isDisplayable());

        SwingUtilities.invokeAndWait(() -> {
            view.getBackButton().doClick();
        });

        assertFalse(view.isDisplayable());
    }
}
