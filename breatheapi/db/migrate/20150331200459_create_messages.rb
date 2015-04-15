class CreateMessages < ActiveRecord::Migration
  def change
    create_table :messages do |t|
      t.string :body
      t.references :user
      t.string :sender_pin

      t.timestamps
    end
  end
end
