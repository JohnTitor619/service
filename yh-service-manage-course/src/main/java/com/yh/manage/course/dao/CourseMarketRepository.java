package com.yh.manage.course.dao;

import com.lxw.framework.domain.course.CourseBase;
import com.lxw.framework.domain.course.CourseMarket;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Administrator.
 */
public interface CourseMarketRepository extends JpaRepository<CourseMarket,String> {
}
