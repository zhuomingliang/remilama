require 'rubygems' unless defined?(Gem)
require 'bundler/setup'
Bundler.require(:default)

$:.concat %w(db/models)
Dir["db/models/**/*.rb"].each{|f| require(File.basename f)}

DataMapper.setup(:default, 'mysql://remilama:remilama@localhost/remilama')
DataMapper.finalize

namespace :remilama do
  namespace :dm do
    task :upgrade do
      DataMapper.auto_upgrade!
    end

    task :migrate do
      DataMapper.auto_migrate!
    end
  end
end
