/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.bugs;

import org.hibernate.bugs.model.Person;
import org.hibernate.bugs.model.Phone;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import java.util.List;

import static org.hibernate.testing.transaction.TransactionUtil.doInHibernate;
import static org.junit.Assert.assertEquals;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework.
 * Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred.
 * Since we nearly always include a regression test with bug fixes, providing your reproducer using this method
 * simplifies the process.
 *
 * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then
 * submit it as a PR!
 */
@TestForIssue( jiraKey = "HHH-10965" )
public class ORMUnitTestCase extends BaseCoreFunctionalTestCase {

	// Add your entities here.
	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class<?>[] {
				Person.class,
				Phone.class,
		};
	}

	// If you use *.hbm.xml mappings, instead of annotations, add the mappings here.
	@Override
	protected String[] getMappings() {
		return new String[] {
//				"Foo.hbm.xml",
//				"Bar.hbm.xml"
		};
	}
	// If those mappings reside somewhere other than resources/org/hibernate/test, change this.
	@Override
	protected String getBaseForMappings() {
		return "org/hibernate/test/";
	}

	// Add in any settings that are specific to your test.  See resources/hibernate.properties for the defaults.
	@Override
	protected void configure(Configuration configuration) {
		super.configure( configuration );

		configuration.setProperty( AvailableSettings.SHOW_SQL, Boolean.TRUE.toString() );
		configuration.setProperty( AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString() );
		//configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
	}

	// Add your tests, using standard JUnit.
	@Test
	public void shouldListPersonsAndPhonesSelectingAllAttributes() throws Exception {
		// works great
		doInHibernate( this::sessionFactory, session -> {
			Person person = new Person();
			person.setId(1L);
			session.persist( person );

			person.addPhone( new Phone( "027-123-4567" ) );
			person.addPhone( new Phone( "028-234-9876" ) );
		} );

		doInHibernate( this::sessionFactory, session -> {
			List<Person> persons = session.createQuery(
					"select distinct p from Person p" )
					.getResultList();
			assertEquals(persons.size(),1);
			assertEquals(persons.get(0).getPhones().size(),2);
		} );
	}

	@Test
	public void shouldListPersonsAndPhonesSelectingSpecificAttributes() throws Exception {
		/*
			returns two person (one for each phone) instead of one
		 */
		doInHibernate( this::sessionFactory, session -> {
			Person person = new Person();
			person.setId(1L);
			session.persist( person );

			person.addPhone( new Phone( "027-123-4567" ) );
			person.addPhone( new Phone( "028-234-9876" ) );
		} );

		doInHibernate( this::sessionFactory, session -> {
			List<Person> persons = session.createQuery(
					"select distinct p.id, ph.number from Person p left join p.phones ph" )
					.getResultList();
			assertEquals(persons.size(),1);
			assertEquals(persons.get(0).getPhones().size(),2);

		} );
	}

	@Test
	public void shouldListPersonsAndPhonesSelectingSpecificAttributesUsingNewOperator() throws Exception {
		/* throws Caused by: org.h2.jdbc.JdbcSQLException: Syntax error in SQL statement "SELECT DISTINCT PERSON0_.ID AS COL_0_0_, .[*] AS COL_1_0_ FROM PERSON PERSON0_ LEFT OUTER JOIN PHONE PHONES1_ ON PERSON0_.ID=PHONES1_.PERSON_ID INNER JOIN PHONE PHONES2_ ON PERSON0_.ID=PHONES2_.PERSON_ID "; expected "*, NOT, EXISTS, INTERSECTS, SELECT, FROM"; SQL statement:
		select distinct person0_.id as col_0_0_, . as col_1_0_ from Person person0_ left outer join Phone phones1_ on person0_.id=phones1_.person_id inner join Phone phones2_ on person0_.id=phones2_.person_id [42001-176]
		at org.h2.message.DbException.getJdbcSQLException(DbException.java:344)
		at org.h2.message.DbException.getSyntaxError(DbException.java:204)
		 */
		doInHibernate( this::sessionFactory, session -> {
			Person person = new Person();
			person.setId(1L);
			session.persist( person );

			person.addPhone( new Phone( "027-123-4567" ) );
			person.addPhone( new Phone( "028-234-9876" ) );
		} );

		doInHibernate( this::sessionFactory, session -> {
			List<Person> persons = session.createQuery(
					"select distinct new org.hibernate.bugs.model.Person(p.id, p.phones) from Person p left join p.phones ph" )
					.getResultList();
			assertEquals(persons.size(),1);
			assertEquals(persons.get(0).getPhones().size(),2);

		} );
	}








}
