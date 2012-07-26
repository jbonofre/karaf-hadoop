/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.karaf.commands;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.namenode.NameNode;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

@Command(scope = "hadoop", name = "namenode-format", description = "Format a HDFS volume")
public class NameNodeFormat extends HadoopCommandSupport {

    @Option(name = "-f", aliases = { "--force" }, description = "Force the format", required = false, multiValued = false)
    boolean force;

    @Override
    protected void doExecute(Configuration conf) throws Exception {
        Collection<File> dirsToFormat = FSNamesystem.getNamespaceDirs(conf);
        for (Iterator<File> it = dirsToFormat.iterator(); it.hasNext(); ) {
            File curDir = it.next();
            if (!curDir.exists())
                continue;
            if (!force) {
                System.err.println("Re-format filesystem in " + curDir + " ? (Y or N)");
                System.err.flush();
                if (!(System.in.read() == 'Y')) {
                    System.err.println("Format aborded in " + curDir);
                    return;
                }
                while (System.in.read() != '\n'); // discard the enter-key
            }
        }
        NameNode.format(conf);
    }

}
