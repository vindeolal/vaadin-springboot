package com.vkd.vaadin.springvaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.util.StringUtils;

@Route
public class MainView extends VerticalLayout {

    private VerticalLayout layout;

    ItemRepository repository;

    ItemEditor itemEditor;

    Grid<Item> grid;

    TextField searchField;

    public MainView(ItemRepository repository, ItemEditor itemEditor) {
        this.repository = repository;
        this.itemEditor = itemEditor;
        this.grid = new Grid<>(Item.class);
        this.searchField = new TextField();

        layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);
        Label label = new Label("Inventory System");
        //label.setHeight("100px");
        label.setSizeFull();
        //label.setWidth("100px");

        layout.add(label);
        searchField.setPlaceholder("Filter by item name");
        searchField.focus();
        Button addButton = new Button("Add new Item", VaadinIcon.PLUS.create());
        HorizontalLayout searchLayout = new HorizontalLayout(searchField, addButton);
        layout.add(searchLayout);

        grid.setHeight("300px");
        grid.setColumns("id", "name", "quantity");
        grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);
        layout.add(grid);

        add(layout, itemEditor);

        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> listItems(e.getValue()));
        addButton.addClickListener(e -> itemEditor.editItem(new Item("", 0)));

        grid.asSingleSelect().addValueChangeListener(e -> {
            itemEditor.editItem(e.getValue());
        });


        itemEditor.setChangeHandler(() -> {
            itemEditor.setVisible(false);
            listItems(searchField.getValue());
        });
        listItems(null);
    }

    private void addItemList() {

        grid.setHeight("300px");
        // grid.setColumns("Id", "Name", "Quantity");
        //grid.get("id").setWidth("50px").setFlexGrow(0);
        layout.add(grid);

        grid.asSingleSelect().addValueChangeListener(e -> {
            itemEditor.editItem(e.getValue());
        });
    }

    private void addSearchBar() {
        searchField.setPlaceholder("Filter by item name");
        searchField.focus();
        Button addButton = new Button("Add new Item", VaadinIcon.PLUS.create());
        HorizontalLayout searchLayout = new HorizontalLayout(searchField, addButton);
        layout.add(searchLayout);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> listItems(e.getValue()));
        addButton.addClickListener(e -> itemEditor.editItem(new Item("", 0)));
    }

    private void addHeader() {
        Label label = new Label("Inventory System");
        label.setSizeFull();
        layout.add(label);
    }

    private void setupLayout() {
        layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);
        add(layout);
    }

    void listItems(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(repository.findAll());
        } else {
            grid.setItems(repository.findByNameStartsWithIgnoreCase(filterText));
        }
    }
}
