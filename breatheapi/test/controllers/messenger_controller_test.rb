require 'test_helper'

class MessengerControllerTest < ActionController::TestCase
  test "should get index" do
    get :index
    assert_response :success
  end

end
