CREATE TABLE `goods` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
	`goods_name` varchar(16) DEFAULT NULL COMMENT '商品名称',
	`goods_title` varchar(64) DEFAULT NULL COMMENT '商品标题',
	`goods_img` varchar(64) DEFAULT NULL COMMENT '商品的图片',
	`goods_detail` longtext COMMENT '商品的详情介绍',
	`goods_price` decimal(10,2) DEFAULT '0.00' COMMENT '商品单价',
	`goods_stock` int(11) DEFAULT '0' COMMENT '商品库存，-1表示没有限制' ,
	PRIMARY KEY(`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4

INSERT INTO `goods` VALUES (1, 'iphoneX', 'Apple iPhone X (A1865) 64GB 银色 移动联通电信4G', '/img/iphoneX.png', 'Apple iPhone X (A1865) 64GB 银色 移动联通电信4G', 8765.00, 10000);
INSERT INTO `goods` VALUES (2, '华为Mate9', '华为Mate 9 4GB+32GB 月光银 移动联通电信4G手机 双卡双待', '/img/mate9.png', '华为Mate 9 4GB+32GB 月光银 移动联通电信4G手机 双卡双待', 3999.00, -1);