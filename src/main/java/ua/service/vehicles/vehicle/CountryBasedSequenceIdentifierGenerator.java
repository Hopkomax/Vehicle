package ua.service.vehicles.vehicle;

import org.hibernate.MappingException;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.QualifiedNameParser;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.enhanced.DatabaseStructure;
import org.hibernate.id.enhanced.Optimizer;
import org.hibernate.id.enhanced.OptimizerFactory;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import ua.service.vehicles.Identifiable;
import ua.service.vehicles.IdentifiableCountry;

import java.io.Serializable;
import java.util.Properties;

@SuppressWarnings("unused")
public class CountryBasedSequenceIdentifierGenerator extends SequenceStyleGenerator implements PersistentIdentifierGenerator, Configurable {

    private DatabaseStructure databaseStructureUa;
    private DatabaseStructure databaseStructureAll;

    private Optimizer optimizerUa;
    private Optimizer optimizerAll;

    private Vehicle vehicle;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        final JdbcEnvironment jdbcEnvironment = serviceRegistry.getService(JdbcEnvironment.class);
        final Dialect dialect = jdbcEnvironment.getDialect();

        boolean forceTableUse = ConfigurationHelper.getBoolean(FORCE_TBL_PARAM, params, false);

        int incrementSize = determineIncrementSize(params);

        final String optimizationStrategy = determineOptimizationStrategy(params, incrementSize);
        incrementSize = determineAdjustedIncrementSize(optimizationStrategy, incrementSize);

        if (dialect.supportsSequences() && !forceTableUse) {
            if (!dialect.supportsPooledSequences() && OptimizerFactory.isPooledOptimizer(optimizationStrategy )) {
                forceTableUse = true;
            }
        }

        this.databaseStructureUa = buildDatabaseStructure(
                type,
                params,
                jdbcEnvironment,
                forceTableUse,
                QualifiedNameParser.INSTANCE.parse(params.getProperty("ua_sequence_name")),
                Integer.parseInt(params.getProperty("ua_initial_value")),
                incrementSize
        );
        this.databaseStructureAll = buildDatabaseStructure(
                type,
                params,
                jdbcEnvironment,
                forceTableUse,
                QualifiedNameParser.INSTANCE.parse(params.getProperty("h_sequence_name")),
                Integer.parseInt(params.getProperty("h_initial_value")),
                incrementSize
        );

        this.optimizerUa = OptimizerFactory.buildOptimizer(
                optimizationStrategy,
                type.getReturnedClass(),
                incrementSize,
                ConfigurationHelper.getInt( INITIAL_PARAM, params, -1 )
        );
        this.optimizerAll = OptimizerFactory.buildOptimizer(
                optimizationStrategy,
                type.getReturnedClass(),
                incrementSize,
                ConfigurationHelper.getInt( INITIAL_PARAM, params, -1 )
        );

        this.databaseStructureUa.prepare(optimizerUa);
        this.databaseStructureAll.prepare(optimizerAll);
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) {
        if (obj instanceof Identifiable) {
            Identifiable identifiable = (Identifiable) obj;
            Serializable id = (Serializable) identifiable.getId();
            if (id != null) {
                return id;
            }
        }

        if (obj instanceof IdentifiableCountry) {
            IdentifiableCountry countryAware = (IdentifiableCountry) obj;
            return generateIdByCountry(session, (String) countryAware.getCountry());
        }
        return null;
        }

    private Serializable generateIdByCountry(SharedSessionContractImplementor session, String vehicle) {
        if (vehicle.equalsIgnoreCase("ua")) {
            return optimizerUa.generate(databaseStructureUa.buildCallback(session));
        } else {
            return optimizerAll.generate(databaseStructureAll.buildCallback(session));
        }
    }

    @Override
    public void registerExportables(Database database) {
        databaseStructureUa.registerExportables(database);
        databaseStructureAll.registerExportables(database);
    }

}
