package jp.loioz.entity;

import java.time.LocalDateTime;

import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;

import jp.loioz.entity.common.DefaultEntity;

/**
 * 
 */
@Entity(listener = SysConfigEntityListener.class)
@Table(name = "sys_config")
public class SysConfigEntity extends DefaultEntity {

	/**  */
	@Id
	@Column(name = "variable")
	String variable;

	/**  */
	@Column(name = "value")
	String value;

	/**  */
	@Column(name = "set_time")
	LocalDateTime setTime;

	/**  */
	@Column(name = "set_by")
	String setBy;

	/**
	 * Returns the variable.
	 * 
	 * @return the variable
	 */
	public String getVariable() {
		return variable;
	}

	/**
	 * Sets the variable.
	 * 
	 * @param variable the variable
	 */
	public void setVariable(String variable) {
		this.variable = variable;
	}

	/**
	 * Returns the value.
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value the value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns the setTime.
	 * 
	 * @return the setTime
	 */
	public LocalDateTime getSetTime() {
		return setTime;
	}

	/**
	 * Sets the setTime.
	 * 
	 * @param setTime the setTime
	 */
	public void setSetTime(LocalDateTime setTime) {
		this.setTime = setTime;
	}

	/**
	 * Returns the setBy.
	 * 
	 * @return the setBy
	 */
	public String getSetBy() {
		return setBy;
	}

	/**
	 * Sets the setBy.
	 * 
	 * @param setBy the setBy
	 */
	public void setSetBy(String setBy) {
		this.setBy = setBy;
	}
}