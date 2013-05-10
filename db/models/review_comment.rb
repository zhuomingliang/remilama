class ReviewComment
  include DataMapper::Resource

  property :id, Serial
  belongs_to :review_document, :child_key => [:review_id, :document_id]
  property :page_number, Integer, :required => true
  property :description, String, :required => true, :length => 2048

  belongs_to :commented_by, 'Player', :required => true
  property :loc_x, Float, :required => true
  property :loc_y, Float, :required => true

  property :commented_at, DateTime, :required => true
end
