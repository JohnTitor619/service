package com.yh;

import com.lxw.framework.domain.cms.CmsPage;
import com.yh.manage.cms.CmsApplication;
import com.yh.manage.cms.dao.CmsPageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = CmsApplication.class)
@RunWith(SpringRunner.class)
public class TestSpringDataMongoDB {
    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Test
    public void testFindAll(){
        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);
    }

    @Test
    public void testAdd(){
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageAliase("yuntaishan");
        cmsPageRepository.insert(cmsPage);
    }

    @Test
    public void testUpdate() {
        Optional<CmsPage> one = cmsPageRepository.findById("634291ccc1444865d4d7084c");
        if (one.isPresent()) {
            CmsPage cmsPage = one.get();
            cmsPage.setPageName("haha");
            cmsPageRepository.save(cmsPage);
        }
    }

    @Test
    public void testDelete() {
        cmsPageRepository.deleteById("634291ccc1444865d4d7084c");
    }
}
