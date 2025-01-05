module org.huseyin.deneme {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.jartool;

    opens org.huseyin.deneme to javafx.fxml;
    exports org.huseyin.deneme;
    exports org.huseyin.deneme.controllers;
    opens org.huseyin.deneme.controllers to javafx.fxml;
}
