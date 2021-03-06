package org.maproulette.client;

import org.apache.commons.lang.StringUtils;
import org.maproulette.client.api.ChallengeAPI;
import org.maproulette.client.api.ProjectAPI;
import org.maproulette.client.api.TaskAPI;
import org.maproulette.client.connection.MapRouletteConfiguration;
import org.maproulette.client.exception.MapRouletteException;
import org.maproulette.client.model.Project;

/**
 * @author mcuthbert
 */
public class IntegrationBase
{
    public static final String DEFAULT_PROJECT_NAME = "IntegrationTestProject";
    public static final String ENVIRONMENT_HOST = "host";
    public static final String ENVIRONMENT_PORT = "port";
    public static final String ENVIRONMENT_API_KEY = "apiKey";
    private MapRouletteConfiguration configuration = null;
    private ProjectAPI projectAPI = null;
    private ChallengeAPI challengeAPI = null;
    private TaskAPI taskAPI = null;
    private final Project defaultProject = Project.builder().name(DEFAULT_PROJECT_NAME)
            .description("Project Description").displayName("Project Displayname").enabled(true)
            .build();
    private long defaultProjectIdentifier = -1;

    public long getDefaultProjectIdentifier()
    {
        return this.defaultProjectIdentifier;
    }

    public Project getDefaultProject()
    {
        return this.defaultProject;
    }

    public TaskAPI getTaskAPI()
    {
        if (this.taskAPI == null)
        {
            this.taskAPI = new TaskAPI(this.getConfiguration());
        }
        return this.taskAPI;
    }

    public ChallengeAPI getChallengeAPI()
    {
        if (this.challengeAPI == null)
        {
            this.challengeAPI = new ChallengeAPI(this.getConfiguration());
        }
        return this.challengeAPI;
    }

    public ProjectAPI getProjectAPI()
    {
        if (this.projectAPI == null)
        {
            this.projectAPI = new ProjectAPI(this.getConfiguration());
        }
        return this.projectAPI;
    }

    public MapRouletteConfiguration getConfiguration()
    {
        if (this.configuration == null)
        {
            var host = System.getenv(ENVIRONMENT_HOST);
            if (StringUtils.isEmpty(host))
            {
                host = "localhost";
            }
            int port;
            try
            {
                port = Integer.parseInt(System.getenv(ENVIRONMENT_PORT));
            }
            catch (final NumberFormatException e)
            {
                port = 9000;
            }
            var apiKey = System.getenv(ENVIRONMENT_API_KEY);
            if (StringUtils.isEmpty(apiKey))
            {
                apiKey = "test";
            }
            this.configuration = new MapRouletteConfiguration(host, port, DEFAULT_PROJECT_NAME,
                    apiKey);
        }
        return this.configuration;
    }

    public void setup() throws MapRouletteException
    {
        // build the project that will be used to execute the integration tests for the challenges
        this.defaultProjectIdentifier = this.projectAPI.create(this.defaultProject).getId();
    }

    public void teardown() throws MapRouletteException
    {
        // remove the project that is used to execute the integration tests for the challenges
        if (this.defaultProjectIdentifier != -1)
        {
            this.projectAPI.forceDelete(this.defaultProjectIdentifier);
        }
    }
}
