class CreateMessages < ActiveRecord::Migration
  def change
    create_table :messages do |t|
      t.string :body
      t.string :from_me

      t.references :conversation
      t.timestamps
    end
  end
end
