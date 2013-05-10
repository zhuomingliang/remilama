class ReviewParticipant
  include DataMapper::Resource

  belongs_to :review, :key => true
  belongs_to :player, :key => true
end
