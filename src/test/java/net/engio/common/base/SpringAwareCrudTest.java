package net.engio.common.base;


import net.engio.common.persistence.Entity;
import net.engio.common.test.CrudTest;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:META-INF/common-applicationContext.xml")
public abstract class SpringAwareCrudTest<K extends Serializable, A extends Entity<K>> extends CrudTest<K,A> {

}
