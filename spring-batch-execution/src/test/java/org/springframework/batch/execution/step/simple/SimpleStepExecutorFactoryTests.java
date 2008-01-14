/*
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.batch.execution.step.simple;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.springframework.batch.core.domain.JobExecution;
import org.springframework.batch.core.domain.JobInstance;
import org.springframework.batch.core.domain.StepExecution;
import org.springframework.batch.core.domain.StepInstance;
import org.springframework.batch.core.domain.StepSupport;
import org.springframework.batch.core.tasklet.Tasklet;
import org.springframework.batch.execution.step.RepeatOperationsHolder;
import org.springframework.batch.execution.step.SimpleStep;
import org.springframework.batch.repeat.ExitStatus;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatOperations;
import org.springframework.batch.repeat.exception.handler.ExceptionHandler;
import org.springframework.batch.repeat.interceptor.RepeatInterceptorAdapter;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;

/**
 * @author Dave Syer
 * 
 */
public class SimpleStepExecutorFactoryTests extends TestCase {

	private SimpleStepExecutorFactory factory = new SimpleStepExecutorFactory();

	protected void setUp() throws Exception {
		factory.setJobRepository(new JobRepositorySupport());
	}

	public void testSuccessfulStepExecutor() throws Exception {
		assertNotNull(factory.getExecutor(new SimpleStep()));
	}

	public void testSuccessfulExceptionHandler() throws Exception {
		SimpleStep configuration = new SimpleStep();
		final List list = new ArrayList();
		configuration.setExceptionHandler(new ExceptionHandler() {
			public void handleException(RepeatContext context,
					Throwable throwable) throws RuntimeException {
				list.add(throwable);
				throw new RuntimeException("Oops");
			}
		});
		SimpleStepExecutor executor = (SimpleStepExecutor) factory
				.getExecutor(configuration);
		StepExecution stepExecution = new StepExecution(new StepInstance(
				new Long(11)), new JobExecution(new JobInstance(null),
				new Long(12)));
		try {
			executor.process(configuration, stepExecution);
			fail("Expected RuntimeException");
		} catch (NullPointerException e) {
			throw e;
		}catch (RuntimeException e) {
			assertEquals("Oops", e.getMessage());
		}
		assertEquals(1, list.size());
	}

	public void testSuccessfulRepeatOperationsHolder() throws Exception {
		RepeatTemplate repeatTemplate = new RepeatTemplate();
		final List list = new ArrayList();
		repeatTemplate.setInterceptor(new RepeatInterceptorAdapter() {
			public void onError(RepeatContext context, Throwable e) {
				list.add(e);
			}
		});
		repeatTemplate.setCompletionPolicy(new SimpleCompletionPolicy(2));
		SimpleHolderStepConfiguration configuration = new SimpleHolderStepConfiguration(
				repeatTemplate);
		SimpleStepExecutor executor = (SimpleStepExecutor) factory
				.getExecutor(configuration);
		StepExecution stepExecution = new StepExecution(new StepInstance(
				new Long(11)), new JobExecution(new JobInstance(null),
				new Long(12)));
		try {
			executor.process(configuration, stepExecution);
			fail("Expected RuntimeException");
		} catch (NullPointerException e) {
			// expected
		}
		assertEquals(1, list.size());
	}

	public void testSuccessfulRepeatOperationsHolderWithStepOperations() throws Exception {
		RepeatTemplate chunkTemplate = new RepeatTemplate();
		final List list = new ArrayList();
		chunkTemplate.setInterceptor(new RepeatInterceptorAdapter() {
			public void before(RepeatContext context) {
				list.add(context);
			}
		});
		chunkTemplate.setCompletionPolicy(new SimpleCompletionPolicy(2));
		RepeatTemplate stepTemplate = new RepeatTemplate();
		final List steps = new ArrayList();
		stepTemplate.setInterceptor(new RepeatInterceptorAdapter() {
			public void before(RepeatContext context) {
				steps.add(context);
			}
		});
		stepTemplate.setCompletionPolicy(new SimpleCompletionPolicy(1));
		SimpleHolderStepConfiguration configuration = new SimpleHolderStepConfiguration(
				chunkTemplate, stepTemplate);
		configuration.setTasklet(new Tasklet() {
			public ExitStatus execute() throws Exception {
				return ExitStatus.CONTINUABLE;
			}
		});
		SimpleStepExecutor executor = (SimpleStepExecutor) factory
				.getExecutor(configuration);
		StepExecution stepExecution = new StepExecution(new StepInstance(
				new Long(11)), new JobExecution(new JobInstance(null),
				new Long(12)));
		executor.process(configuration, stepExecution);
		assertEquals(2, list.size());
		assertEquals(1, steps.size());
	}

	public void testUnsuccessfulWrongConfiguration() throws Exception {
		try {
			factory.getExecutor(new StepSupport());
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			// expected
			assertTrue(
					"Error message does not contain SimpleStepConfiguration: "
							+ e.getMessage(), e.getMessage().indexOf(
							"SimpleStepConfiguration") >= 0);
		}
	}

	public void testUnsuccessfulNoJobRepository() throws Exception {
		try {
			factory = new SimpleStepExecutorFactory();
			factory.getExecutor(new SimpleStep());
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
			assertTrue("Error message does not contain JobRepository: "
					+ e.getMessage(),
					e.getMessage().indexOf("JobRepository") >= 0);
		}
	}

	public void testMandatoryProperties() throws Exception {
		factory = new SimpleStepExecutorFactory();
		try {
			factory.afterPropertiesSet();
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	/**
	 * @author Dave Syer
	 * 
	 */
	public class SimpleHolderStepConfiguration extends SimpleStep
			implements RepeatOperationsHolder {
		private RepeatOperations chunkOperations;
		private RepeatOperations stepOperations;

		public SimpleHolderStepConfiguration(RepeatOperations operations) {
			this.chunkOperations = operations;
		}

		public SimpleHolderStepConfiguration(RepeatOperations chunkOperations, RepeatOperations stepOperations) {
			this.chunkOperations = chunkOperations;
			this.stepOperations = stepOperations;
		}

		public RepeatOperations getChunkOperations() {
			return chunkOperations;
		}

		public RepeatOperations getStepOperations() {
			return stepOperations;
		}
	}

}
