package ogs.selenium.listeners;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.testng.IAnnotationTransformer3;
import org.testng.ITestNGListener;
import org.testng.annotations.IConfigurationAnnotation;
import org.testng.annotations.IDataProviderAnnotation;
import org.testng.annotations.IFactoryAnnotation;
import org.testng.annotations.IListenersAnnotation;
import org.testng.annotations.ITestAnnotation;
import org.testng.internal.annotations.IAnnotationTransformer;
import ogs.testng.dataprovider.CDataProvider;
import ogs.testng.dataprovider.DataProviderUtil;
import ogs.testng.dataprovider.CDataProvider.dataproviders;


public class DataTestListener implements IAnnotationTransformer, IAnnotationTransformer3, ITestNGListener {

	@Override
	public void transform(ITestAnnotation testAnnotation, Class testClass, Constructor testConstructor, Method method) {
		try {
			if ((method.getAnnotation(CDataProvider.class) != null) && (null != method.getParameterTypes())
					&& (null != method) && (method.getParameterTypes().length > 0)) {
				CDataProvider dp = method.getAnnotation(CDataProvider.class);
				if(dp.dataFile().contains("xlsx") || dp.dataFile().contains("xls")){
					testAnnotation.setDataProvider(dataproviders.isfw_excel.name());
					testAnnotation.setDataProviderClass(DataProviderUtil.class);
				}else{
					testAnnotation.setDataProvider(dataproviders.isfw_database.name());
					testAnnotation.setDataProviderClass(DataProviderUtil.class);
				}
			}

			// testAnnotation.setRetryAnalyzer( );

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void transform(IConfigurationAnnotation annotation, @SuppressWarnings("rawtypes") Class testClass, @SuppressWarnings("rawtypes") Constructor testConstructor,
			Method testMethod) {

	}

	@Override
	public void transform(IDataProviderAnnotation annotation, Method method) {

	}

	@Override
	public void transform(IFactoryAnnotation annotation, Method method) {
		// TODO Auto-generated method stub
		annotation.setDataProvider(dataproviders.isfw_excel.name());
		annotation.setDataProviderClass(DataProviderUtil.class);
	}

	@Override
	public void transform(IListenersAnnotation annotation, Class testClass) {

	}

	/*
	 * @Override public List<IMethodInstance> intercept(List<IMethodInstance>
	 * methods, ITestContext context) { // TODO Auto-generated method stub
	 * context.getSuite().addListener(new DataTestListener()); return methods; }
	 */

}
