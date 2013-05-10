class ReviewDocument
  include DataMapper::Resource
 
  belongs_to :review, :key => true
  belongs_to :document, :key => true
end
