class Player
  include DataMapper::Resource

  property :id, Serial
  property :name, String, :length => 256
  property :email, String, :length => 256
end
