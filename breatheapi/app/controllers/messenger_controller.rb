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
        p params
        @user = User.find_by(pin: params[:pin])
        @message = Message.new(body: params[:body])
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
        p params[:secret]
        p @user.secret
        p @user.secret == params[:secret]
        if params[:secret] != @user.secret
            render :json => {response: "STOP TRYING TO HACK US"} 
        else 
            queue = []
            if num_messages_on_client >= @user.messages.length
                render :json => {message: "No new messages 4 U."}
            else 
                @user.messages[num_messages_on_client-1...-1].each do |new_message|
                    queue << {body: new_message.body}
                end
                render :json => {messages: queue}
            end
        end
    end
end
