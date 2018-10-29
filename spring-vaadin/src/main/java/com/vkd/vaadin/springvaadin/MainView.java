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

        addLayout();
        addLabel();
        addFilterAndAddFields();
        addItemGrid();

        add(layout, itemEditor);

        itemEditor.setChangeHandler(() -> {
            itemEditor.setVisible(false);
            listItems(searchField.getValue());
        });

        //for initial grid view
        listItems(null);
    }

    //create layout for UI
    private void addLayout() {
        layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);
    }

    //add label to layout
    private void addLabel() {
        Label label = new Label("Inventory System");
        label.getStyle().set("font-size", "32px");
        label.setWidth(null);
        label.setHeight("70px");
        layout.add(label);
        layout.setHorizontalComponentAlignment(Alignment.CENTER, label);
    }

    //add filter field and button to add new items
    private void addFilterAndAddFields() {
        searchField.setPlaceholder("Filter by item name");
        searchField.focus();
        Button addButton = new Button("Add new Item", VaadinIcon.PLUS.create());
        HorizontalLayout searchLayout = new HorizontalLayout(searchField, addButton);
        layout.add(searchLayout);

        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> listItems(e.getValue()));
        addButton.addClickListener(e -> itemEditor.editItem(new Item("", 0)));
    }

    //create a grid to display all the items
    private void addItemGrid() {
        grid.setHeight("300px");
        grid.setColumns("id", "name", "quantity");
        grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);
        layout.add(grid);
        grid.asSingleSelect().addValueChangeListener(e -> {
            itemEditor.editItem(e.getValue());
        });
    }

    //fetch items based on item name passed
    private void listItems(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(repository.findAll());
        } else {
            grid.setItems(repository.findByNameStartsWithIgnoreCase(filterText));
        }
    }
}
