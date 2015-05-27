class CreateUsers < ActiveRecord::Migration
  def change
    create_table :users do |t|
      t.string :pin
      t.integer :message_count
      t.string :secret
      t.string :key

      t.timestamps
    end
  end
end
