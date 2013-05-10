class DocumentPage
  include DataMapper::Resource

  belongs_to :document, :key => true
  property :page_number, Integer, :key => true

  property :filename, String, :length => 64, :required => true
  property :width, Integer, :required => true
  property :height, Integer, :required => true
end
