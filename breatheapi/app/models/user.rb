class User < ActiveRecord::Base
    has_many :conversations
    has_many :messages
end
