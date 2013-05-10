class Review
  include DataMapper::Resource

  property :id, Serial
  property :name, String, :required => true
  property :review_date, Date
  property :begin_at, Integer
  property :end_at, Integer
  
  belongs_to :reviewee, 'Player'

  has n, :participants, 'ReviewParticipant'
  has n, :review_documents
  has n, :documents, :through => :review_documents
end
