package com.lxw.framework.domain.ucenter.ext;

import com.lxw.framework.domain.course.ext.CategoryNode;
import com.lxw.framework.domain.ucenter.Menu;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created by admin on 2018/3/20.
 */
@Data
@ToString
public class XcMenuExt extends Menu {

    List<CategoryNode> children;
}
