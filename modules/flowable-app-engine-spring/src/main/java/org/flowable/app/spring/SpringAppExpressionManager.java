/* Licensed under the Apache License, Version 2.0 (the "License");
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

package org.flowable.app.spring;

import java.util.Map;

import org.flowable.app.engine.impl.el.AppExpressionManager;
import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.el.DefaultExpressionManager;
import org.flowable.common.engine.impl.el.JsonNodeELResolver;
import org.flowable.common.engine.impl.el.ReadOnlyMapELResolver;
import org.flowable.common.engine.impl.javax.el.ArrayELResolver;
import org.flowable.common.engine.impl.javax.el.BeanELResolver;
import org.flowable.common.engine.impl.javax.el.CompositeELResolver;
import org.flowable.common.engine.impl.javax.el.ELResolver;
import org.flowable.common.engine.impl.javax.el.ListELResolver;
import org.flowable.common.engine.impl.javax.el.MapELResolver;
import org.springframework.context.ApplicationContext;

/**
 * {@link DefaultExpressionManager} that exposes the full application-context or a limited set of beans in expressions.
 * 
 * @author Tijs Rademakers
 */
public class SpringAppExpressionManager extends AppExpressionManager {

    protected ApplicationContext applicationContext;

    /**
     * @param applicationContext
     *            the applicationContext to use. Ignored when 'beans' parameter is not null.
     * @param beans
     *            a map of custom beans to expose. If null, all beans in the application-context will be exposed.
     */
    public SpringAppExpressionManager(ApplicationContext applicationContext, Map<Object, Object> beans) {
        super(beans);
        this.applicationContext = applicationContext;
    }

    @Override
    protected ELResolver createElResolver(VariableContainer variableContainer) {
        CompositeELResolver compositeElResolver = new CompositeELResolver();
        compositeElResolver.add(createVariableElResolver(variableContainer));

        if (beans != null) {
            // Only expose limited set of beans in expressions
            compositeElResolver.add(new ReadOnlyMapELResolver(beans));
        } else {
            // Expose full application-context in expressions
            compositeElResolver.add(new ApplicationContextElResolver(applicationContext));
        }

        compositeElResolver.add(new ArrayELResolver());
        compositeElResolver.add(new ListELResolver());
        compositeElResolver.add(new MapELResolver());
        compositeElResolver.add(new JsonNodeELResolver());
        compositeElResolver.add(new BeanELResolver());
        return compositeElResolver;
    }

}
