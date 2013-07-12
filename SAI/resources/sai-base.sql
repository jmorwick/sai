DROP TABLE IF EXISTS `feature_isa_relationships`;
CREATE TABLE `feature_isa_relationships` (
  `parent_id` int(11) NOT NULL COMMENT 'tag (node_instances->id)',
  `feature_id` int(11) NOT NULL COMMENT 'tag (node_instances->id)',
  UNIQUE KEY `parent_id` (`parent_id`),
  UNIQUE KEY `feature_id` (`feature_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `graph_instances`;
CREATE TABLE `graph_instances` (
  `id` int(11) NOT NULL auto_increment,
  `nodes` int(11) NOT NULL,
  `edges` int(11) NOT NULL,
  `features` int(11) NOT NULL COMMENT 'number of associated features',
  `is_index` BOOLEAN NOT NULL,
  `checked` BOOLEAN NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `is_index` (`is_index`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='instances of graphs';


DROP TABLE IF EXISTS `node_instances`;
CREATE TABLE `node_instances` (
  `id` int(11) NOT NULL COMMENT 'unique within a graph, not globally unique',
  `graph_id` int(11) NOT NULL COMMENT 'foreign key (graph_instances->id)',
  `features` int(11) NOT NULL COMMENT 'number of associated features',
  KEY `id` (`id`,`graph_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='instance of a node in a graph instance';


DROP TABLE IF EXISTS `edge_instances`;
CREATE TABLE `edge_instances` (
  `id` int(11) NOT NULL COMMENT 'unique within a graph, not globally unique',
  `graph_id` int(11) NOT NULL COMMENT 'foreign key (graph_instances->id)',
  `from_node_id` int(11) NOT NULL COMMENT 'output node id (node_instances->id)',
  `to_node_id` int(11) NOT NULL COMMENT 'input node id (node_instances->id)',
  `features` int(11) NOT NULL COMMENT 'number of associated features',
  KEY `id` (`id`,`graph_id`),
  KEY `graph_id` (`graph_id`,`from_node_id`,`to_node_id`),
  KEY `from_node_id` (`graph_id`,`from_node_id`),
  KEY `to_node_id` (`graph_id`,`to_node_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='edge between two nodes';

DROP TABLE IF EXISTS `feature_instances`;
CREATE TABLE `feature_instances` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(256) NOT NULL COMMENT 'unique name of a tag within a class',
  `featureclass` varchar(256) NOT NULL COMMENT 'unique name of a class of tags',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`, `featureclass`),
  KEY `featureclass` (`featureclass`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `node_features`;
CREATE TABLE `node_features` (
  `graph_id` int(11) NOT NULL COMMENT 'tagged graph (graph_instances->id)',
  `node_id` int(11) NOT NULL COMMENT 'tagged node (node_instances->id)',
  `feature_id` int(11) NOT NULL COMMENT 'tag (node_instances->id)',
  KEY `node_id` (`graph_id`,`node_id`),
  KEY `feature_id` (`feature_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='associates tags to nodes';


DROP TABLE IF EXISTS `graph_features`;
CREATE TABLE `graph_features` (
  `graph_id` int(11) NOT NULL COMMENT 'tagged graph (graph_instances->id)',
  `feature_id` int(11) NOT NULL COMMENT 'tag (node_instances->id)',
  KEY `graph_id` (`graph_id`),
  KEY `feature_id` (`feature_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='associates tags to nodes';


DROP TABLE IF EXISTS `edge_features`;
CREATE TABLE `edge_features` (
  `graph_id` int(11) NOT NULL COMMENT 'tagged graph (graph_instances->id)',
  `edge_id` int(11) NOT NULL COMMENT 'tagged edge (edge_instances->id)',
  `feature_id` int(11) NOT NULL COMMENT 'tag (node_instances->id)',
  KEY `edge_id` (`graph_id`,`edge_id`),
  KEY `feature_id` (`feature_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='associates tags to nodes';


DROP TABLE IF EXISTS `graph_indices`;
CREATE TABLE `graph_indices` (
  `index_id` int(11) NOT NULL COMMENT 'id of index (graph) (graph_instances->id)',
  `graph_id` int(11) NOT NULL COMMENT 'indexed graph (graph_instances->id)',
  `instances` int(11) COMMENT 'maximum number of occurances of the indexed feature in this structure (null indicates not determined)',
  KEY `index_id` (`index_id`),
  KEY `graph_id` (`graph_id`),
  KEY `combined_id` (`index_id`, `graph_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='associates graphs to indices';
