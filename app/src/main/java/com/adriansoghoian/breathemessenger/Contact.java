package com.adriansoghoian.breathemessenger;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by adrian on 4/15/15.
 */
@Table(name= "Contacts")
public class Contact extends Model {

    @Column(name = "pin")
    public String pin;

    @Column(name = "Name")
    public String name;

    @Column(name = "PubKey")
    public String pubKey;

    public Contact() {
        super();
    }

    public Contact(String pin, String name, String pubKey) {
        super();
        this.pin = pin;
        this.name = name;
        this.pubKey = pubKey;
    }

    public List<Conversation> conversations(Contact contact) {
        return new Select()
                .from(Conversation.class)
                .where("contact = ?", contact.getId())
                .execute();
    }

    public static List<Contact> getAllContacts() {
        return new Select()
                .from(Contact.class)
                .execute();
    }
}
