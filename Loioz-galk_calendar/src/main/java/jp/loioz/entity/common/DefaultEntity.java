package jp.loioz.entity.common;

public class DefaultEntity {

	   
	  // 更新パターンの初期値は更新 
	  /** 更新パターン（true=更新,false=論理削除） */ 
	  private boolean updatePattern = true; 
	 
	  /**  
	   * Returns the updatePattern. 
	   *  
	   * @return the updatePattern 
	   */ 
	  public boolean getUpdatePattern() { 
	    return updatePattern; 
	  } 
	 
	  /**  
	   * 更新パターンを更新に設定 
	   *  
	   * @param updatePattern the updatePattern 
	   */ 
	  public void setFlgUpdate() { 
	    this.updatePattern = true; 
	  } 
	 
	  /**  
	   * 更新パターンを論理削除に設定 
	   *  
	   * @param updatePattern the updatePattern 
	   */ 
	  public void setFlgDelete() { 
	    this.updatePattern = false; 
	  } 
}
