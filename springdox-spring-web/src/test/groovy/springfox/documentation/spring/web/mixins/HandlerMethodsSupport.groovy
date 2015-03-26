package springfox.documentation.spring.web.mixins
import com.fasterxml.classmate.MemberResolver
import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.members.ResolvedMethod
import org.springframework.web.method.HandlerMethod
import springfox.documentation.spring.web.dummy.DummyClass

trait HandlerMethodsSupport {
  HandlerMethod methodWithChild() {
    def clazz = new DummyClass.MethodsWithSameName()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod("methodToTest", Integer, DummyClass.Child))
  }

  ResolvedMethod resolvedMethod() {
    def typeResolver = new TypeResolver()
    ResolvedType dummy = typeResolver.resolve(DummyClass)
    def memberResolver = new MemberResolver(typeResolver)
    def resolvedMembers = memberResolver.resolve(dummy, null, null)
    return resolvedMembers.getMemberMethods().find { "methodThatIsHidden".equals(it.getName())}
  }

  HandlerMethod methodWithParent() {
    def clazz = new DummyClass.MethodsWithSameName()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod("methodToTest", Integer, DummyClass.Parent))
  }

  HandlerMethod methodOnDummyClasss(String method, Class ... params) {
    def clazz = new DummyClass()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod(method, params))
  }
}
