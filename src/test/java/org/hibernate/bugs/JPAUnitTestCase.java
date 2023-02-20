package org.hibernate.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.hibernate.model.Child;
import org.hibernate.model.Parent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaQuery;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		Parent parent = new Parent();
		entityManager.persist(parent);
		entityManager.persist(new Child(parent, "b"));
		entityManager.persist(new Child(parent, "a"));
		entityManager.persist(new Child(parent, "c"));

		entityManager.getTransaction().commit();
		entityManager.close();


		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		// expect children to be ordered by name but are ordered by insertion ordered
		// children are ordered correctly when use FetchMode.JOIN or FetchMode.SELECT
		CriteriaQuery<Parent> query = entityManager.getCriteriaBuilder().createQuery(Parent.class);
		query.select(query.from(Parent.class));
		List<Child> children = entityManager.createQuery(query).getResultList().get(0).getChildren();
		assertEquals("a", children.get(0).getName());
		assertEquals("b", children.get(1).getName());
		assertEquals("c", children.get(2).getName());

		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
