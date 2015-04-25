package com.adriansoghoian.breathemessenger;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by adrian on 4/15/15.
 */
@Table(name = "Conversations")
public class Conversation extends Model {

    @Column(name = "Contact", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public Contact contact;

    public Conversation() {
        super();
    }

    public Conversation(Contact contact) {
        super();
        this.contact = contact;
    }

    public static List<Conversation> getAll() {
        return new Select()
                .from(Conversation.class)
                .execute();
    }


}
