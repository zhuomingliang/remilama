class Document
  include DataMapper::Resource

  property :id, Serial
  property :name, String, :length => 256
  property :page_count, Integer
  property :created_at, DateTime
  property :vcs_version, String, :length => 50

  has n, :document_pages
end
