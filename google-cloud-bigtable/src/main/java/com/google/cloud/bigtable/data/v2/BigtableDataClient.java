/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigtable.data.v2;

import com.google.api.core.InternalApi;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.ServerStream;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.bigtable.admin.v2.InstanceName;
import com.google.cloud.bigtable.data.v2.stub.EnhancedBigtableStub;
import com.google.cloud.bigtable.data.v2.wrappers.Query;
import com.google.cloud.bigtable.data.v2.wrappers.Row;
import com.google.cloud.bigtable.data.v2.wrappers.RowAdapter;
import java.io.IOException;

/**
 * Client for reading from and writing to existing Bigtable tables.
 *
 * <p>This class provides the ability to make remote calls to the backing service. Sample code to
 * get started:
 *
 * <pre>{@code
 * InstanceName instanceName = InstanceName.of("[PROJECT]", "[INSTANCE]");
 * try (BigtableDataClient bigtableDataClient = BigtableDataClient.create(instanceName)) {
 *   for(Row row : bigtableDataClient.readRows(Query.create("[TABLE]")) {
 *     // Do something with row
 *   }
 * }
 * }</pre>
 *
 * <p>Note: close() needs to be called on the bigtableDataClient object to clean up resources such
 * as threads. In the example above, try-with-resources is used, which automatically calls close().
 *
 * <p>The surface of this class includes several types of Java methods for each of the API's
 * methods:
 *
 * <ol>
 *   <li>A "flattened" method, like `readRows()`. With this type of method, the fields of the
 *       request type have been converted into function parameters. It may be the case that not all
 *       fields are available as parameters, and not every API method will have a flattened method
 *       entry point.
 *   <li>A "callable" method, like `readRowsCallable()`. This type of method takes no parameters and
 *       returns an immutable API callable object, which can be used to initiate calls to the
 *       service.
 * </ol>
 *
 * <p>See the individual methods for example code.
 *
 * <p>This class can be customized by passing in a custom instance of BigtableDataSettings to
 * create(). For example:
 *
 * <p>To customize credentials:
 *
 * <pre>{@code
 * BigtableDataSettings bigtableDataSettings =
 *     BigtableDataSettings.newBuilder()
 *         .setInstanceName(InstanceName.of("[PROJECT]", "[INSTANCE]"))
 *         .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
 *         .build();
 * try(BigtableDataClient bigtableDataClient = BigtableDataClient.create(bigtableDataSettings)) {
 *   // ..
 * }
 * }</pre>
 *
 * To customize the endpoint:
 *
 * <pre>{@code
 * BigtableDataSettings bigtableDataSettings =
 *     BigtableDataSettings.newBuilder()
 *       .setInstanceName(InstanceName.of("[PROJECT]", "[INSTANCE]"))
 *       .setEndpoint(myEndpoint).build();
 * try(BigtableDataClient bigtableDataClient = BigtableDataClient.create(bigtableDataSettings)) {
 *   // ..
 * }
 * }</pre>
 */
public class BigtableDataClient implements AutoCloseable {
  private final EnhancedBigtableStub stub;

  /**
   * Constructs an instance of BigtableClient with default settings.
   *
   * @param instanceName The instance to connect to.
   * @return A new client.
   * @throws IOException If any.
   */
  public static BigtableDataClient create(InstanceName instanceName) throws IOException {
    BigtableDataSettings settings =
        BigtableDataSettings.newBuilder().setInstanceName(instanceName).build();
    return create(settings);
  }

  /**
   * Constructs an instance of BigtableDataClient, using the given settings. The channels are
   * created based on the settings passed in, or defaults for any settings that are not set.
   */
  public static BigtableDataClient create(BigtableDataSettings settings) throws IOException {
    EnhancedBigtableStub stub = EnhancedBigtableStub.create(settings.getTypedStubSettings());
    return new BigtableDataClient(stub);
  }

  @InternalApi("Visible for testing")
  BigtableDataClient(EnhancedBigtableStub stub) {
    this.stub = stub;
  }

  /**
   * Convenience method for synchronous streaming the results of a {@link Query}.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // Import the filter DSL
   * import static com.google.cloud.bigtable.data.v2.wrappers.Filters.FILTERS;
   *
   * InstanceName instanceName = InstanceName.of("[PROJECT]", "[INSTANCE]");
   * try (BigtableClient bigtableClient = BigtableClient.create(instanceName)) {
   *   String tableId = "[TABLE]";
   *
   *   Query query = Query.create(tableId)
   *          .range("[START KEY]", "[END KEY]")
   *          .filter(FILTERS.qualifier().regex("[COLUMN PREFIX].*"));
   *
   *   // Iterator style
   *   for(Row row : bigtableClient.readRows(query)) {
   *     // Do something with row
   *   }
   * }
   * }</pre>
   *
   * @see ServerStreamingCallable For call styles.
   * @see Query For query options.
   * @see com.google.cloud.bigtable.data.v2.wrappers.Filters For the filter building DSL.
   */
  public ServerStream<Row> readRows(Query query) {
    return readRowsCallable().call(query);
  }

  /**
   * Convenience method for asynchronous streaming the results of a {@link Query}.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * InstanceName instanceName = InstanceName.of("[PROJECT]", "[INSTANCE]");
   * try (BigtableClient bigtableClient = BigtableClient.create(instanceName)) {
   *   String tableId = "[TABLE]";
   *
   *   Query query = Query.create(tableId)
   *          .range("[START KEY]", "[END KEY]")
   *          .filter(FILTERS.qualifier().regex("[COLUMN PREFIX].*"));
   *
   *   client.readRowsAsync(query, new ResponseObserver<Row>() {
   *     public void onStart(StreamController controller) { }
   *     public void onResponse(Row response) {
   *       // Do something with Row
   *     }
   *     public void onError(Throwable t) {
   *       // Handle error before the stream completes
   *     }
   *     public void onComplete() {
   *       // Handle stream completion
   *     }
   *   });
   * }
   * }</pre>
   */
  public void readRowsAsync(Query query, ResponseObserver<Row> observer) {
    readRowsCallable().call(query, observer);
  }

  /**
   * Streams back the results of the query. The returned callable object allows for customization of
   * api invocation.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * InstanceName instanceName = InstanceName.of("[PROJECT]", "[INSTANCE]");
   * try (BigtableClient bigtableClient = BigtableClient.create(instanceName)) {
   *   String tableId = "[TABLE]";
   *
   *   Query query = Query.create(tableId)
   *          .range("[START KEY]", "[END KEY]")
   *          .filter(FILTERS.qualifier().regex("[COLUMN PREFIX].*"));
   *
   *   // Iterator style
   *   for(Row row : bigtableClient.readRowsCallable().call(query)) {
   *     // Do something with row
   *   }
   *
   *   // Point look up
   *   ApiFuture<Row> rowFuture = bigtableClient.readRowsCallable().first().futureCall(query);
   *
   *   // etc
   * }
   * }</pre>
   *
   * @see ServerStreamingCallable For call styles.
   * @see Query For query options.
   * @see com.google.cloud.bigtable.data.v2.wrappers.Filters For the filter building DSL.
   */
  public ServerStreamingCallable<Query, Row> readRowsCallable() {
    return stub.readRowsCallable();
  }

  /**
   * Streams back the results of the query. This callable allows for customization of the logical
   * representation of a row. It's meant for advanced use cases.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * InstanceName instanceName = InstanceName.of("[PROJECT]", "[INSTANCE]");
   * try (BigtableClient bigtableClient = BigtableClient.create(instanceName)) {
   *   String tableId = "[TABLE]";
   *
   *   Query query = Query.create(tableId)
   *          .range("[START KEY]", "[END KEY]")
   *          .filter(FILTERS.qualifier().regex("[COLUMN PREFIX].*"));
   *
   *   // Iterator style
   *   for(CustomRow row : bigtableClient.readRowsCallable(new CustomRowAdapter()).call(query)) {
   *     // Do something with row
   *   }
   * }
   * }</pre>
   *
   * @see ServerStreamingCallable For call styles.
   * @see Query For query options.
   * @see com.google.cloud.bigtable.data.v2.wrappers.Filters For the filter building DSL.
   */
  public <RowT> ServerStreamingCallable<Query, RowT> readRowsCallable(RowAdapter<RowT> rowAdapter) {
    return stub.createReadRowsCallable(rowAdapter);
  }

  /** Close the clients and releases all associated resources. */
  @Override
  public void close() throws Exception {
    stub.close();
  }
}
