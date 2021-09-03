import React from 'react';
import clsx from 'clsx';
import styles from './HomepageFeatures.module.css';

const FeatureList = [
  {
    title: 'Data Driven',
    Svg: require('../../static/img/undraw_docusaurus_mountain.svg').default,
    description: (
      <>
          Patchouli books integrate tightly with vanilla systems such as datapack recipes
          and advancements.
      </>
    ),
  },
  {
    title: 'Dependency-Free',
    Svg: require('../../static/img/undraw_docusaurus_tree.svg').default,
    description: (
      <>
          Patchouli requires no other mods to be installed. Simply download Patchouli,
          and start authoring content.
      </>
    ),
  },
  {
    title: 'Tried and Trusted',
    Svg: require('../../static/img/undraw_docusaurus_react.svg').default,
    description: (
      <>
          Patchouli powers the documentation of critically acclaimed mods such
          as <a href="https://botaniamod.net">Botania</a>.
      </>
    ),
  },
];

function Feature({Svg, title, description}) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} alt={title} />
      </div>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
