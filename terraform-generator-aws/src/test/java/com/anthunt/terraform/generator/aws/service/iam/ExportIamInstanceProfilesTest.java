package com.anthunt.terraform.generator.aws.service.iam;

import com.anthunt.terraform.generator.aws.client.AmazonClients;
import com.anthunt.terraform.generator.aws.support.DisabledOnNoAwsCredentials;
import com.anthunt.terraform.generator.aws.support.TestDataFileUtils;
import com.anthunt.terraform.generator.aws.utils.JsonUtils;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Maps;
import com.anthunt.terraform.generator.core.model.terraform.nodes.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.GetRolePolicyResponse;
import software.amazon.awssdk.services.iam.model.InstanceProfile;
import software.amazon.awssdk.services.iam.model.Role;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(classes = {AmazonClients.class})
class ExportIamInstanceProfilesTest {

    private static ExportIamInstanceProfiles exportIamInstanceProfiles;

    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeAll
    public static void beforeAll() {
        exportIamInstanceProfiles = new ExportIamInstanceProfiles();
    }

    @Test
    @DisabledOnNoAwsCredentials
    void export() {
        AmazonClients amazonClients = AmazonClients.builder().profileName("default").region(Region.AWS_GLOBAL).build();
        IamClient iamClient = amazonClients.getIamClient();

        Maps<Resource> export = exportIamInstanceProfiles.export(iamClient, null, null);

        log.debug("result => \n{}", export.unmarshall());
    }

    @Test
    public void getResourceMaps() {
        List<InstanceProfile> instanceProfiles = List.of(
                InstanceProfile.builder()
                        .instanceProfileName("eks-7cbddf86-c0a6-643b-dbdd-85b97c390535")
                        .roles(Role.builder().roleName("eks-cluster-workernode-role").build())
                        .build(),
                InstanceProfile.builder()
                        .instanceProfileName("role-packer-base")
                        .roles(Role.builder().roleName("role-packer-base").build(),
                                Role.builder().roleName("role-packer-base2").build())
                        .build()

        );
        Maps<Resource> resourceMaps = exportIamInstanceProfiles.getResourceMaps(instanceProfiles);
        String actual = resourceMaps.unmarshall();

        log.debug("actual => \n{}", actual);
        String expected = TestDataFileUtils.asString(
                resourceLoader.getResource("testData/iam/expected/IamInstanceProfile.tf")
        );
        assertEquals(expected, actual);
    }

}