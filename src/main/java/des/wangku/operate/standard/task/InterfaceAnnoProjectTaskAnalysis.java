package des.wangku.operate.standard.task;

import des.wangku.operate.standard.utls.UtilsDate;

/**
 * 解析任务状态注解信息
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceAnnoProjectTaskAnalysis {
	/**
	 * 得到注解对象，通过反射得到
	 * @return AnnoProjectTask
	 */
	public default AnnoProjectTask getAnnoProjectTask() {
		return this.getClass().getAnnotation(AnnoProjectTask.class);
	}
	/**
	 * 从注解中提出项目组名
	 * @return String
	 */
	public default String getAnnoGroup() {
		AnnoProjectTask anno = getAnnoProjectTask();
		if (anno == null) return "";
		return anno.group();
	}

	/**
	 * 从注解中提出名称编号
	 * @return String
	 */
	public default String getAnnoIdentifier() {
		AnnoProjectTask anno = getAnnoProjectTask();
		if (anno == null) return null;
		return anno.identifier();
	}

	/**
	 * 从注解中提出名称
	 * @return String
	 */
	public default String getAnnoName() {
		AnnoProjectTask anno = getAnnoProjectTask();
		if (anno == null) return null;
		return anno.name();
	} 
	/**
	 * 从注解中提出状态
	 * @return boolean
	 */
	public default boolean getAnnoExpire() {
		AnnoProjectTask anno = getAnnoProjectTask();
		if (anno == null) return false;
		return anno.expire();
	}
	/**
	 * 从注解中提出有效期，并判断是否在有效期之内
	 * @return boolean
	 */
	public default boolean isEffective() {
		AnnoProjectTask anno = getAnnoProjectTask();
		if (anno == null) return true;
		String time1=anno.dateStart();
		String time2=anno.dateEnd();				
		return UtilsDate.isEffectiveStandard(time1, time2);
	}
}
