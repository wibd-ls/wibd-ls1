package wibd.ls.ml.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="patent_error_report_compare")
public class ImageErrorReport {
	private String image_path;
	private Integer year;
	private Integer patent;
	@DynamoDBHashKey(attributeName="image_path")
	public String getImage_path() {
		return image_path;
	}
	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}
	@DynamoDBAttribute(attributeName="year")
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	@DynamoDBAttribute(attributeName="patent")
	public Integer getPatent() {
		return patent;
	}
	public void setPatent(Integer patent) {
		this.patent = patent;
	}
	
}
