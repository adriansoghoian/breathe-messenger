require 'securerandom'

class User < ActiveRecord::Base
    has_many :messages
    validates_uniqueness_of :pin, :secret
    after_initialize :init

    def init
        self.secret ||= SecureRandom.hex(50)
        self.save
    end
end
