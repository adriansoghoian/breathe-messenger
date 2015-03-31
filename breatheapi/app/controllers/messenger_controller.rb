class MessengerController < ApplicationController
    def index
    end

    def test
        p params
        render :json => { sup: "yo"}
    end

end
