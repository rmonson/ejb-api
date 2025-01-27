/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package javax.ejb;

import java.util.*;
import java.security.Identity;
import java.security.Principal;
import javax.transaction.UserTransaction;

/**
 * The EJBContext interface provides an instance with access to the 
 * container-provided runtime context of an enterprise bean instance. 
 *
 * <p> This interface is extended by the <code>SessionContext</code>, 
 * <code>EntityContext</code>, and <code>MessageDrivenContext</code> interfaces
 * to provide additional methods specific to the enterprise interface bean type.
 * 
 * @see SessionContext
 * @see MessageDrivenContext
 * @see EntityContext
 *
 * @since EJB 1.0
 */
public interface EJBContext
{
    /**
     * Obtain the enterprise bean's remote home interface.
     *
     * @return The enterprise bean's remote home interface.
     *
     * @exception java.lang.IllegalStateException if the enterprise bean 
     * does not have a remote home interface.
     */
    EJBHome getEJBHome() throws IllegalStateException;

    /**
     * Obtain the enterprise bean's local home interface.
     *
     * @return The enterprise bean's local home interface.
     *
     * @exception java.lang.IllegalStateException if the enterprise bean 
     * does not have a local home interface.
     *
     * @since EJB 2.0
     */
    EJBLocalHome getEJBLocalHome() throws IllegalStateException;

    /**
     * Obtain the enterprise bean's environment properties.
     * 
     * <p><b>Note:</b> If the enterprise bean has no environment properties 
     * this method returns an empty <code>java.util.Properties</code> object. 
     * This method never returns <code>null</code>.
     *
     * @return The environment properties for the enterprise bean.
     *
     * @deprecated Use the JNDI naming context java:comp/env to access
     *    enterprise bean's environment.
     */
    Properties getEnvironment();

    /**
     * Obtain the <code>java.security.Identity</code> of the caller.
     *
     * This method is deprecated in enterprise bean 1.1. The Container
     * is allowed to return always <code>null</code> from this method. The enterprise
     * bean should use the <code>getCallerPrincipal</code> method instead.
     *
     * @return The <code>Identity</code> object that identifies the caller.
     *
     * @deprecated Use Principal getCallerPrincipal() instead.
     */
    Identity getCallerIdentity();

 
    /**
     * Obtain the <code>java.security.Principal</code> that identifies the caller.
     * 
     * @return The <code>Principal</code> object that identifies the caller. This
     *    method never returns <code>null</code>.
     *
     * @exception IllegalStateException The Container throws the exception
     *    if the instance is not allowed to call this method.
     *
     * @since EJB 1.1
     */
    Principal getCallerPrincipal() throws IllegalStateException;

    /**
     * Test if the caller has a given role.
     *
     * <p>This method is deprecated in enterprise bean 1.1. The enterprise bean
     * should use the <code>isCallerInRole(String roleName)</code> method instead.
     *
     * @param role The <code>java.security.Identity</code> of the role to be tested.
     *
     * @return True if the caller has the specified role.
     *
     * @deprecated Use boolean isCallerInRole(String roleName) instead.
     */
    boolean isCallerInRole(Identity role);

    /**  
     * Test if the caller has a given security role.
     *   
     * @param roleName The name of the security role. The role must be one of
     *    the security roles that is defined in the deployment descriptor.
     *   
     * @return True if the caller has the specified role.
     *
     * @exception IllegalStateException The Container throws the exception
     *    if the instance is not allowed to call this method.
     *
     * @since EJB 1.1
     */  
    boolean isCallerInRole(String roleName) throws IllegalStateException;

    /**
     * Obtain the transaction demarcation interface.
     *
     * Only enterprise beans with bean-managed transactions are allowed to
     * to use the <code>UserTransaction</code> interface. As entity beans must always use
     * container-managed transactions, only session beans or message-driven
     * beans with bean-managed transactions are allowed to invoke this method. 
     *
     * @return The <code>UserTransaction</code> interface that the enterprise bean
     *    instance can use for transaction demarcation.
     *
     * @exception IllegalStateException The Container throws the exception
     *    if the instance is not allowed to use the <code>UserTransaction</code> interface
     *    (i.e. the instance is of a bean with container-managed transactions).
     */
    UserTransaction getUserTransaction() throws IllegalStateException;

    /**
     * Mark the current transaction for rollback. The transaction will become
     * permanently marked for rollback. A transaction marked for rollback
     * can never commit.
     *
     * Only enterprise beans with container-managed transactions are allowed
     * to use this method.
     *
     * @exception IllegalStateException The Container throws the exception
     *    if the instance is not allowed to use this method (i.e. the
     *    instance is of a bean with bean-managed transactions).
     */
    void setRollbackOnly() throws IllegalStateException;

    /**
     * Test if the transaction has been marked for rollback only. An enterprise
     * bean instance can use this operation, for example, to test after an
     * exception has been caught, whether it is fruitless to continue
     * computation on behalf of the current transaction.
     *
     * Only enterprise beans with container-managed transactions are allowed
     * to use this method.
     *
     * @return True if the current transaction is marked for rollback, false
     *   otherwise.
     *
     * @exception IllegalStateException The Container throws the exception
     *    if the instance is not allowed to use this method (i.e. the
     *    instance is of a bean with bean-managed transactions).
     */
    boolean getRollbackOnly() throws IllegalStateException;

    /**
     * Get access to the enterprise bean Timer Service.
     *
     * @exception IllegalStateException The Container throws the exception
     *    if the instance is not allowed to use this method (e.g. if the bean
     *    is a stateful session bean)
     *
     * @since EJB 2.1
     */
    TimerService getTimerService() throws IllegalStateException;

    /**
     * Lookup a resource within the <code>java:</code> namespace.  Names referring to
     * entries within the private component namespace can be passed as
     * unqualified strings.  In that case the lookup will be relative to
     * <code>"java:comp/env/"</code>.
     *
     * For example, assuming an enterprise bean defines an <code>ejb-local-ref</code>
     * with <code>ejb-ref-name</code> <code>"ejb/BarRef"</code> the following two 
     * calls to <code> EJBContext.lookup</code> are equivalent :
     *
     *  <code>ejbContext.lookup("ejb/BarRef")</code>;
     *  <code>ejbContext.lookup("java:comp/env/ejb/BarRef")</code>;
     *
     * @param name Name of the entry 
     *
     * @exception IllegalArgumentException The Container throws the exception
     *    if the given name does not match an entry within the component's
     *    environment.
     *
     * @since EJB 3.0
     */
    Object lookup(String name) throws IllegalArgumentException;

    /**
     * The <code>getContextData</code> method enables a business method, lifecycle 
     * callback method, or timeout method to retrieve any interceptor/webservices context 
     * associated with its invocation.
     * 
     * @return the context data that interceptor context associated with this invocation. 
     * If there is no context data, an empty <code>Map&#060;String,Object&#062;</code> 
     * object will be returned.
     *
     * @since EJB 3.1
     */
    Map<String, Object> getContextData();

}
