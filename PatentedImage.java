package wibd.ls.ml.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="patent_info_compare")
public class PatentedImage {
	private Integer patent;
	private Integer year;
	private String image_path;
	private String second_image_path;
	private String third_image_path;
	private String fourth_image_path;
	private String fifth_image_path;
	@DynamoDBAttribute(attributeName="third_image_path")
	public String getThird_image_path() {
		return third_image_path;
	}
	public void setThird_image_path(String third_image_path) {
		this.third_image_path = third_image_path;
	}
	@DynamoDBAttribute(attributeName="fourth_image_path")
	public String getFourth_image_path() {
		return fourth_image_path;
	}
	public void setFourth_image_path(String fourth_image_path) {
		this.fourth_image_path = fourth_image_path;
	}
	@DynamoDBAttribute(attributeName="fifth_image_path")
	public String getFifth_image_path() {
		return fifth_image_path;
	}
	public void setFifth_image_path(String fifth_image_path) {
		this.fifth_image_path = fifth_image_path;
	}
	private String same_patent_n_year;
	@DynamoDBHashKey(attributeName="patent") 
	public Integer getPatent() {
		return patent;
	}
	public void setPatent(Integer patent) {
		this.patent = patent;
	}
	@DynamoDBRangeKey(attributeName="year")
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	@DynamoDBAttribute(attributeName="image_path") 
	public String getImage_path() {
		return image_path;
	}
	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}
	@DynamoDBAttribute(attributeName="second_image_path")
	public String getSecond_image_path() {
		return second_image_path;
	}
	public void setSecond_image_path(String second_image_path) {
		this.second_image_path = second_image_path;
	}
	@DynamoDBAttribute(attributeName="same_patent_n_year")
	public String getSame_patent_n_year() {
		return same_patent_n_year;
	}
	public void setSame_patent_n_year(String same_patent_n_year) {
		this.same_patent_n_year = same_patent_n_year;
	}
	
	public void setEmptyImagePath(String imagePath) {
		if(image_path == null) {
			setImage_path(imagePath);
		}else if(second_image_path == null) {
			setSecond_image_path(imagePath);
		}else if(third_image_path == null) {
			setThird_image_path(imagePath);
		}else if(fourth_image_path == null) {
			setFourth_image_path(imagePath);
		}else if(fifth_image_path == null) {
			setFifth_image_path(imagePath);
		}
	}
}
