package image.example.agility;


import org.junit.Test;

public class AgilityTest {

	public AgilityTest() {

	}

	@Test
	public void contextLoads() {

		String[] args = {"-m=67","-u=http://10.200.131.235:17080/webservices/common/agility/getImage","-c=20180302","-v=11","-s=18","-d","-f=./ai.jpeg"};

		Agility.main(args);

	}

}
