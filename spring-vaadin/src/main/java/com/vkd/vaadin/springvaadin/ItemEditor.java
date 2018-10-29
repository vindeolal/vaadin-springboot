package com.vkd.vaadin.springvaadin;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class ItemEditor extends VerticalLayout implements KeyNotifier {

    private ItemRepository itemRepository;
    private Item item;

    //fields to save new item
    private TextField name = new TextField();
    private TextField quantity = new TextField();

    //create all action buttons
    private Button save = new Button("Save", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Cancel", VaadinIcon.ARROWS_CROSS.create());
    private Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    private HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    //binder for Item class
    private Binder<Item> binder = new Binder<>(Item.class);

    private ChangeHandler changeHandler;

    @Autowired
    public ItemEditor(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
        add(name, quantity, actions);
        //integer field binding
        binder.forField(quantity)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Item::getQuantity, Item::setQuantity);
        binder.bindInstanceFields(this);

        setSpacing(true);

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> editItem(item));
        setVisible(false);

    }

    //delete operation
    void delete() {
        itemRepository.delete(item);
        changeHandler.itemChanged();
    }

    //save operation
    void save() {
        itemRepository.save(item);
        changeHandler.itemChanged();
    }

    //to edit and item in grid
    public final void editItem(Item i) {
        if (i == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = i.getId() != null;
        if (persisted) {
            // Find fresh entity for editing
            item = itemRepository.findById(i.getId()).get();
        } else {
            item = i;
        }
        cancel.setVisible(persisted);

        // Bind customer properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(item);

        setVisible(true);

        // Focus first name initially
        name.focus();
    }

    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        changeHandler = h;
    }


}
