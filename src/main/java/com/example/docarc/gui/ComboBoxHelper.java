package com.example.docarc.gui;

import com.example.docarc.be.Box;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ComboBoxHelper {
    public static <T> void makeSearchable(ComboBox<T> comboBox, ObservableList<T> list) {
        FilteredList<T> filteredBoxList = new FilteredList<>(list, p -> true);
        comboBox.setItems(filteredBoxList);
        comboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                filteredBoxList.setPredicate(box -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String text = newValue.toLowerCase();
                    String boxLower = box.toString().toLowerCase();

                    if (boxLower.contains(text)) {
                        return true;
                    }
                    return false;
                });
                if (!filteredBoxList.isEmpty() && !comboBox.isShowing()) {
                    comboBox.show();
                }
            });
        });
        ComboBoxListViewSkin<T> customSkin = new ComboBoxListViewSkin<>(comboBox);
        comboBox.setSkin(customSkin);

        customSkin.getPopupContent().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE) {
                event.consume(); //Prevent auto selection
            }
        });
    }
}
