class MessengerController < ApplicationController
    def index
    end

    def new_user
        p params
        @user = User.new(pin: params[:pin])
        if @user.save
            render :json => {response: "New user saved.", secret: @user.secret}
        else
            render :json => {response: "User already exists."}
        end
    end

    def new_message
        p "Receiving a new message. Here are the params: "
        p params
        p "*"*50
        @user = User.find_by(pin: params[:pin])
        @message = Message.new(body: params[:body], sender_pin: params[:senderPin])
        p "Here is the new message object being created:"
        p @message
        p "*"*50
        if @message.save
            @message.save
            @user.messages << @message
            @user.save
            render :json => {response: "Message received."}
        else 
            render :json => {response: "Message could not be saved."}
        end
    end

    def refresh_messages
        num_messages_on_client = params[:message_count].to_i
        @user = User.find_by(pin: params[:pin])
        if params[:secret] != @user.secret
            render :json => {response: "STOP TRYING TO HACK US"} 
        else 
            queue = []
            if num_messages_on_client >= @user.messages.length
                render :json => {message: "No new messages 4 U."}
            else
                p "Refreshing messages; looks like there are undelivered messages."
                p "Here they are: "
                @user.messages[(num_messages_on_client)..-1].each do |new_message|
                    p "Message: "
                    p new_message
                    p "*"*10
                    queue << {body: new_message.body, sender_pin: new_message.sender_pin}
                end
                render :json => {messages: queue}
            end
        end
    end

    def fetch_key 
        p params
    end
end
