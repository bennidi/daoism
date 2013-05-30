package net.engio.common.persistence.dao.query;

import java.util.LinkedList;
import java.util.List;


public abstract class Query{

	public static enum Type{
		Native, Named, Jpql
	}

	public static NamedQuery Named(String queryName){
		return new NamedQuery(queryName);
	}

	public static JpqlQuery Jpql(String queryString){
		return new JpqlQuery(queryString);
	}

	public static NativeQuery NativeSql(String queryString){
		return new NativeQuery(queryString);
	}

	private int maxResults = -1;

	private int firstResult = -1;

	public <T extends Query> T setMaxResults(int bound){
		maxResults = bound;
		return (T)this;
	}
	

	public <T extends Query> T setFirstResult(int bound){
		firstResult = bound;
		return (T)this;
	}

	public boolean isFirstResultSet(){
		return firstResult > 0;
	}

	public int getMaxResults(){
		return maxResults;
	}

	public int getFirtResult(){
		return firstResult;
	}

	public boolean hasResultLimit(){
		return maxResults > 0;
	}

	public abstract Type getType();

	public abstract static class ParametrizedQuery extends Query{

		private List<QueryParameter> parameters = new LinkedList<QueryParameter>();

		private ParametrizedQuery(){

		}

		public QueryParameter<? extends ParametrizedQuery> set(String key){
			return new QueryParameter<ParametrizedQuery>(key);
		}

		public List<QueryParameter> getParameters(){
			return  parameters;
		}

		protected <T extends ParametrizedQuery> T addParameter(QueryParameter param){
			parameters.add(param);
			return (T) this;
		}

		public class QueryParameter<T extends ParametrizedQuery> {

			private String key;

			private Object value;

			private QueryParameter(String key){
				this.key = key;
			}

			public Object getValue(){return value;}

			public String getKey(){return key;}

			public T to(Object value){
				this.value = value;
				return (T)ParametrizedQuery.this.addParameter(this);
			}

		}


	}

	public abstract static class TypedQuery extends ParametrizedQuery{

	}

	public static class NamedQuery extends TypedQuery{

		private String name;

		private NamedQuery(String name){
			this.name = name;
		}

		public String getName(){
			  return name;
		}

		public QueryParameter<NamedQuery> set(String key){
			return new QueryParameter<NamedQuery>(key);
		}

		@Override
		public NamedQuery setMaxResults(int bound){
			return super.setMaxResults(bound);
		}

		@Override
		public NamedQuery setFirstResult(int bound){
			return super.setFirstResult(bound);
		}


		@Override
		public Type getType() {
			return Type.Named;
		}
	}

	public static class JpqlQuery extends TypedQuery{

		private String query;

		private JpqlQuery(String query){
			this.query = query;
		}

		public String getQueryString(){
			  return query;
		}

		public QueryParameter<JpqlQuery> set(String key){
			return new QueryParameter<JpqlQuery>(key);
		}

		@Override
		public JpqlQuery setMaxResults(int bound){
			return super.setMaxResults(bound);
		}

		@Override
		public JpqlQuery setFirstResult(int bound){
			return super.setFirstResult(bound);
		}


		@Override
		public Type getType() {
			return Type.Jpql;
		}
	}

	public static class NativeQuery extends ParametrizedQuery{

		private String query;

		private NativeQuery(String query){
			this.query = query;
		}

		public String getQueryString(){
			  return query;
		}

		public QueryParameter<NativeQuery> set(String key){
			return new QueryParameter<NativeQuery>(key);
		}

		@Override
		public NativeQuery setMaxResults(int bound){
			return super.setMaxResults(bound);
		}

		@Override
		public NativeQuery setFirstResult(int bound){
			return super.setFirstResult(bound);
		}


		@Override
		public Type getType() {
			return Type.Native;
		}
	}



}

