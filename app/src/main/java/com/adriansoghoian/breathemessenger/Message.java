package com.adriansoghoian.breathemessenger;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.query.Join;
import com.activeandroid.Model.*;

import java.util.List;

/**
 * Created by adrian on 4/15/15.
 */
@Table(name = "Messages")
public class Message extends Model {

    @Column(name = "body")
    public String body;

    @Column(name = "Contact", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public Contact contact;

    @Column(name = "Conversation")
    public Conversation conversation;

    public Message() {
       super();
    }

    public Message(String body, Contact contact, Conversation conversation) {
        super();
        this.body = body;
        this.contact = contact;
        this.conversation = conversation;
    }

    public static List<Message> getAll(Conversation convo) {
        return new Select()
                .from(Message.class)
                .where("conversation = ?", convo.getId())
                .execute();
    }

}
