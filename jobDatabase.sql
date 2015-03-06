-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 06, 2015 at 12:28 PM
-- Server version: 5.5.41-0ubuntu0.14.04.1
-- PHP Version: 5.5.9-1ubuntu4.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `jobDatabase`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `getNumOfJobsByCategoryByMonthsInYear`(IN `id_category` INT(5))
begin
SELECT DATE_FORMAT(job.publish_date,'%Y-%m') month, count(job.id) jobSum FROM `job`
join (select * from job_category where category_id=id_category) catHelp on catHelp.job_id=job.id
where date(job.publish_date) like '2014%'
group by DATE_FORMAT(job.publish_date,'%Y-%m')
order by DATE_FORMAT(job.publish_date,'%Y-%m');
end$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getPercentages`(IN `startDate` DATE, IN `endDate` DATE, IN `tableName` VARCHAR(20))
    NO SQL
begin
SET @sumCat = 0;
SET @sumQuery = CONCAT('
SET @sumCat = (select count(',tableName,'_id) from job_',tableName,'
join job on job_',tableName,'.job_id=job.id
where date(publish_date) between ? and ?)');

PREPARE stmt FROM @sumQuery;
SET @startDate = startDate;
SET @endDate = endDate;
EXECUTE stmt USING @startDate,@endDate;
DEALLOCATE PREPARE stmt;


SET @resultSet = CONCAT('
select ',tableName,'.id, round(coalesce(percentage,0),2) percentage from ',tableName,'
left join (
SELECT ',tableName,'_id, (count(*) / @sumCat)*100 percentage FROM job_',tableName,'
join job on job_',tableName,'.job_id=job.id
where date(publish_date) between ? and ?
group by ',tableName,'_id) help on help.',tableName,'_id=',tableName,'.id 
order by ',tableName,'.id'
);
                        
    PREPARE stmt FROM @resultSet;
	SET @startDate = startDate;
	SET @endDate = endDate;
	EXECUTE stmt USING @startDate,@endDate;
	DEALLOCATE PREPARE stmt;

end$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `it_zagreb_danas`()
BEGIN
	SELECT job.title Job, employer.name Employer, job.publish_date, county.name County, REPLACE(job.link, 'http://m', 'http://www') Link FROM `job` 
	join job_category on job_category.job_id = job.id
	join employer on employer.id=job.employer_id
	join job_county on job_county.job_id = job.id
	join county on county.id=job_county.county_id
	WHERE job_category.category_id=11 AND job_county.county_id=2 AND Date(`publish_date`) like CURDATE();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `skillTrend`(IN `xml` TEXT, IN `startDate` DATE, IN `endDate` DATE, IN `countyId` INT)
    NO SQL
BEGIN
    declare v_row_index int unsigned default 0;   
    declare v_row_count int unsigned; 
	declare skillId int unsigned; 
	declare dynWhereCountyId varchar(50) default '';
 
    -- calculate the number of row elements.
	set v_row_count = ExtractValue(xml, 'count(//value)');

	-- create temp table for months
	CREATE TEMPORARY TABLE months (
    	month varchar(10) 
	);

	insert into months
		select DATE_FORMAT(job.publish_date,'%Y-%m') from job
		where date(publish_date) between startDate and endDate
		group by DATE_FORMAT(job.publish_date,'%Y-%m');

	-- create temp table for months
	CREATE TEMPORARY TABLE idsAndMonths (
        id int,
    	month varchar(10) 
	);

	set v_row_index = 0;
    -- loop through all the row elements    
    while v_row_index < v_row_count do
        set v_row_index = v_row_index + 1;
		set skillId = ExtractValue(xml, '//value[$v_row_index]');
		insert into idsAndMonths
			select
				skillId as id,
				month from months;
    end while;

	IF (countyId > 0) THEN
		SET dynWhereCountyId = CONCAT(' and jc.county_id = ', countyId);
	END IF;	

	-- actual query for getting results
	SET @resultSet = CONCAT('
	select im.id as id, im.month as month, coalesce(help.counter, 0) as counter from idsAndMonths im 
	left join
	(SELECT js.skill_id, DATE_FORMAT(j.publish_date,"%Y-%m") as month, count(*) as counter FROM job j
	join job_skill js on js.job_id = j.id
    join job_county jc on j.id = jc.job_id
	where date(j.publish_date) between "', startDate,'" and "', endDate, '"', dynWhereCountyId, ' 
	group by js.skill_id, DATE_FORMAT(j.publish_date,"%Y-%m")
	order by js.skill_id, DATE_FORMAT(j.publish_date,"%Y-%m")) help
	on im.month = help.month and im.id = help.skill_id
    ');

    PREPARE stmt FROM @resultSet;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE IF NOT EXISTS `category` (
  `id` int(11) NOT NULL,
  `name` varchar(50) CHARACTER SET cp1250 COLLATE cp1250_croatian_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `county`
--

CREATE TABLE IF NOT EXISTS `county` (
  `id` int(11) NOT NULL,
  `name` varchar(50) CHARACTER SET cp1250 COLLATE cp1250_croatian_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `employer`
--

CREATE TABLE IF NOT EXISTS `employer` (
  `id` int(11) NOT NULL,
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `link` varchar(400) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `job`
--

CREATE TABLE IF NOT EXISTS `job` (
  `id` int(11) NOT NULL,
  `title` varchar(300) COLLATE utf8mb4_unicode_ci NOT NULL,
  `employer_id` int(11) NOT NULL,
  `publish_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deadline` date NOT NULL,
  `link` varchar(400) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `conditions` text COLLATE utf8mb4_unicode_ci,
  `yearsOfExperience` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `languages` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `skills` text COLLATE utf8mb4_unicode_ci,
  `employerOffer` text COLLATE utf8mb4_unicode_ci,
  `drivingLicence` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `employer_id` (`employer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `jobtype`
--

CREATE TABLE IF NOT EXISTS `jobtype` (
  `id` int(11) NOT NULL,
  `name` varchar(50) CHARACTER SET cp1250 COLLATE cp1250_croatian_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `job_category`
--

CREATE TABLE IF NOT EXISTS `job_category` (
  `job_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL,
  PRIMARY KEY (`job_id`,`category_id`),
  KEY `category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `job_county`
--

CREATE TABLE IF NOT EXISTS `job_county` (
  `job_id` int(11) NOT NULL,
  `county_id` int(11) NOT NULL,
  PRIMARY KEY (`job_id`,`county_id`),
  KEY `county_id` (`county_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `job_jobtype`
--

CREATE TABLE IF NOT EXISTS `job_jobtype` (
  `job_id` int(11) NOT NULL,
  `jobType_id` int(11) NOT NULL,
  PRIMARY KEY (`job_id`,`jobType_id`),
  KEY `jobType_id` (`jobType_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `job_qualification`
--

CREATE TABLE IF NOT EXISTS `job_qualification` (
  `job_id` int(11) NOT NULL,
  `qualification_id` int(11) NOT NULL,
  PRIMARY KEY (`job_id`,`qualification_id`),
  KEY `qualification_id` (`qualification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `job_rating`
--

CREATE TABLE IF NOT EXISTS `job_rating` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `job_id` int(11) NOT NULL,
  `rating_type_id` tinyint(1) unsigned NOT NULL,
  `rating_num` int(10) unsigned NOT NULL,
  `rating_sum` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `job_id` (`job_id`,`rating_type_id`),
  KEY `rating_type_id` (`rating_type_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=113839 ;

-- --------------------------------------------------------

--
-- Table structure for table `job_skill`
--

CREATE TABLE IF NOT EXISTS `job_skill` (
  `job_id` int(11) NOT NULL,
  `skill_id` int(11) NOT NULL,
  PRIMARY KEY (`job_id`,`skill_id`),
  KEY `skill_id` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `job_skill_help`
--

CREATE TABLE IF NOT EXISTS `job_skill_help` (
  `job_id` int(11) NOT NULL,
  `skill_id` int(11) NOT NULL,
  PRIMARY KEY (`job_id`,`skill_id`),
  KEY `skill_id` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `qualification`
--

CREATE TABLE IF NOT EXISTS `qualification` (
  `id` int(11) NOT NULL,
  `name` varchar(50) CHARACTER SET cp1250 COLLATE cp1250_croatian_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `rating_type`
--

CREATE TABLE IF NOT EXISTS `rating_type` (
  `id` tinyint(1) unsigned NOT NULL,
  `rating_name` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `skill`
--

CREATE TABLE IF NOT EXISTS `skill` (
  `id` int(11) NOT NULL,
  `name` varchar(50) CHARACTER SET cp1250 COLLATE cp1250_croatian_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Stand-in structure for view `total_job_rating`
--
CREATE TABLE IF NOT EXISTS `total_job_rating` (
`job_id` int(11)
,`rating` decimal(35,2)
);
-- --------------------------------------------------------

--
-- Structure for view `total_job_rating`
--
DROP TABLE IF EXISTS `total_job_rating`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `total_job_rating` AS select `job_rating`.`job_id` AS `job_id`,round(coalesce((sum(`job_rating`.`rating_sum`) / sum(`job_rating`.`rating_num`)),0),2) AS `rating` from `job_rating` group by `job_rating`.`job_id`;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `job`
--
ALTER TABLE `job`
  ADD CONSTRAINT `job_ibfk_1` FOREIGN KEY (`employer_id`) REFERENCES `employer` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `job_category`
--
ALTER TABLE `job_category`
  ADD CONSTRAINT `job_category_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `job_category_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `job_county`
--
ALTER TABLE `job_county`
  ADD CONSTRAINT `job_county_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `job_county_ibfk_2` FOREIGN KEY (`county_id`) REFERENCES `county` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `job_jobtype`
--
ALTER TABLE `job_jobtype`
  ADD CONSTRAINT `job_jobType_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `job_jobType_ibfk_2` FOREIGN KEY (`jobType_id`) REFERENCES `jobtype` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `job_qualification`
--
ALTER TABLE `job_qualification`
  ADD CONSTRAINT `job_qualification_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `job_qualification_ibfk_2` FOREIGN KEY (`qualification_id`) REFERENCES `qualification` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `job_rating`
--
ALTER TABLE `job_rating`
  ADD CONSTRAINT `job_rating_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `job_rating_ibfk_2` FOREIGN KEY (`rating_type_id`) REFERENCES `rating_type` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `job_skill`
--
ALTER TABLE `job_skill`
  ADD CONSTRAINT `job_skill_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `job_skill_ibfk_2` FOREIGN KEY (`skill_id`) REFERENCES `skill` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
